# Smart Pantry Manager

An Android app, written in Java, that helps reduce food waste. You record the ingredients you actually have at home, and the app suggests only the recipes you can cook right now, with no shopping trip required.

| | |
|---|---|
| **Student** | Jaudaan Deoraj |
| **Student number** | 402411516 |
| **Module** | Mobile App Development 700 |
| **Date** | 28 October 2026 |
| **Repository** | https://github.com/jody79154/Smart_Pantry_Manager |

## Problem and target user

Food gets thrown away because people forget what is in the cupboard, and recipe apps usually suggest dishes that need a trip to the shop. Smart Pantry Manager is for home cooks, students and small households who want to cook from what they already own. A recipe is suggested only when **every** ingredient it needs is available in the required quantity.

## Main features

- **Pantry management:** add, edit and delete ingredients with a name, quantity, unit and an optional expiry date. Data is stored in SQLite and survives closing the app.
- **Pantry list:** a RecyclerView with a custom adapter, an empty-pantry message, edit and delete buttons per row, and a confirmation dialog before deleting.
- **Form validation:** a blank name, a non-numeric or non-positive quantity, a missing unit and an impossible expiry date are all rejected, with the error shown beside the field.
- **20 seeded recipes:** created the first time the database is made, never duplicated on later launches.
- **Suggested Recipes:** runs the strict-matching rule against the current pantry and lists only recipes that can be cooked now. When nothing qualifies it shows "No recipes match your pantry yet—add more ingredients."
- **Recipe Detail:** the full ingredient list and numbered method for a recipe, opened with an Intent that carries the recipe ID.
- **Settings:** an expiring-soon alerts switch and a 3, 5 or 7 day warning window, stored in SharedPreferences. Warnings appear on the pantry list.
- **Navigation:** a bottom navigation bar on the three main screens (Pantry, Recipes, Settings).

### Screens

1. Pantry List
2. Add / Edit Ingredient
3. Suggested Recipes
4. Recipe Detail
5. Settings

## Technology stack

| Item | Choice |
|---|---|
| Language | Java only (no Kotlin) |
| IDE | Android Studio |
| Layouts | XML (ConstraintLayout, LinearLayout, ScrollView) |
| UI libraries | AndroidX AppCompat 1.6.1, Material Components 1.11.0, RecyclerView 1.3.2, ConstraintLayout 2.1.4 |
| Database | SQLite through `SQLiteOpenHelper` |
| Preferences | SharedPreferences |
| Navigation | Explicit Intents (record IDs passed as `long` extras) and a BottomNavigationView |
| Build | Android Gradle Plugin 8.5.2, Gradle 8.7, Java 17 source compatibility |
| SDK | minSdk 26, targetSdk 34, compileSdk 34 |
| Tests | JUnit 4.13.2 (local unit tests) |

The app uses **no** Kotlin, Google Maps or mapping SDK, location or GPS, payment features, notification permission or other third-party libraries. The manifest declares no permissions.

## Database choice: SQLite

SQLite was chosen because the app's data is small and belongs to one user on one device, so no server, account or network connection is needed. It ships with Android, works offline, and `SQLiteOpenHelper` lets the whole life cycle (create, seed, upgrade) live in one class that is easy to follow and explain. The recipe matching needs relational data (a recipe has many ingredients), which SQLite handles with foreign keys. Firebase and PostgreSQL would add accounts, hosting and a network dependency that this app does not need.

### Database structure

Database file `smart_pantry.db`, version 1, foreign keys enabled.

```
pantry_items                         recipes
------------------------------       ------------------------------
id            INTEGER PK AUTOINC     id      INTEGER PK AUTOINC
name          TEXT NOT NULL          name    TEXT NOT NULL UNIQUE
quantity      REAL NOT NULL (>0)     steps   TEXT NOT NULL
unit          TEXT NOT NULL                |
expiry_date   TEXT (nullable)              | 1
                                           |
                                           | many
                                     recipe_ingredients
                                     ------------------------------
                                     id              INTEGER PK AUTOINC
                                     recipe_id       INTEGER NOT NULL  -> recipes(id) ON DELETE CASCADE
                                     ingredient_name TEXT NOT NULL
                                     quantity        REAL NOT NULL (>0)
                                     unit            TEXT NOT NULL
```

- `pantry_items` is independent. Expiry dates are stored as `yyyy-MM-dd` text, or NULL when none is given.
- The seeded data is 20 recipes and 93 recipe-ingredient rows.
- Recipes are seeded inside `onCreate`, with a guard that skips seeding if recipes already exist, so reopening the app never duplicates them.
- Settings are not in the database. They are in SharedPreferences (`smart_pantry_settings`): `expiry_alerts_enabled` and `expiry_warning_days`.

### Package structure

```
com.example.smartpantry
  data/       DatabaseHelper, PantryDao, RecipeDao, RecipeSeeder, RecipeCatalog
  model/      PantryItem, Recipe, RecipeIngredient
  matching/   IngredientNormalizer, UnitConverter, IngredientConversions, RecipeMatcher
  ui/         PantryListActivity, AddEditIngredientActivity, SuggestedRecipesActivity,
              RecipeDetailActivity, SettingsActivity, PantryAdapter, RecipeAdapter,
              BottomNavigationHelper
  util/       InputValidator, QuantityFormatter, IngredientFormatter, ExpiryHelper, SettingsStore
```

The `model`, `matching`, `data/RecipeCatalog` and most `util` classes contain no Android imports, so they run as plain Java unit tests.

## Strict recipe matching

The matching rule is the core of the app and lives in `RecipeMatcher`. A recipe appears in Suggested Recipes **only if every ingredient** it needs is present in the pantry in at least the required quantity. If a recipe needs 5 ingredients and the pantry has 4 of them, it is not shown. There are no partial matches and no "almost there" list.

### How it decides

1. **Normalise names** (`IngredientNormalizer`): lowercase, trim, collapse spaces, remove punctuation and bracketed notes, singularise the last word (`tomatoes` becomes `tomato`, `berries` becomes `berry`), then apply the alias list below.
2. **Parse units** (`UnitConverter`) and convert each quantity to a common base amount.
3. **Convert across unit types where the ingredient is known** (`IngredientConversions`): an ingredient with a known density can be compared by weight whether it was entered in grams, millilitres or spoons, and an ingredient with a known item weight can be compared by weight whether it was entered as a count or as grams.
4. **Combine** duplicate pantry rows for the same ingredient and the same measure into one total.
5. **Check every requirement.** A recipe that needs the same ingredient twice has its requirements added together, so it cannot count the pantry twice. A recipe with no ingredients is never suggested. A small tolerance (1e-9) stops rounding errors rejecting an exact amount.

### Recognised units

| Type | Unit | Base amount | Accepted spellings |
|---|---|---|---|
| Mass | Gram | 1 g | g, gm, gms, gram, grams |
| Mass | Kilogram | 1000 g | kg, kgs, kilo, kilos, kilogram, kilograms |
| Volume | Millilitre | 1 ml | ml, mls, millilitre, millilitres, milliliter, milliliters |
| Volume | Litre | 1000 ml | l, ltr, litre, litres, liter, liters |
| Volume | Teaspoon | 5 ml | tsp, tsps, teaspoon, teaspoons |
| Volume | Tablespoon | 15 ml | tbsp, tbsps, tbs, tablespoon, tablespoons |
| Volume | Cup | 240 ml | cup, cups |
| Count | Item | 1 item | item, items, piece, pieces, pc, pcs, whole, unit, units, each, clove, cloves |

The Add / Edit form offers eight units: g, kg, ml, l, tsp, tbsp, cup and item. The other spellings are recognised by the matcher so that variations such as `grams` or `pieces` still work.

Units from different types (for example grams and tablespoons) can only be compared for ingredients listed in the conversion tables below. For any other ingredient they are treated as incompatible and the recipe is not suggested.

### Ingredient aliases

These names are treated as the same ingredient (after plural handling):

| Treated as | Also accepted |
|---|---|
| spring onion | scallion, green onion |
| bell pepper | capsicum, sweet pepper |
| zucchini | courgette |
| eggplant | aubergine |
| arugula | rocket |
| chickpea | garbanzo, garbanzo bean, chick pea |
| chili | chilli |
| garlic | garlic clove |
| flour | plain flour, all purpose flour |
| sugar | caster sugar, granulated sugar, white sugar |
| baking soda | bicarbonate of soda, bicarb |
| cornstarch | cornflour |
| cream | double cream, heavy cream |
| milk | whole milk, full cream milk |
| yogurt | yoghurt, plain yogurt, natural yogurt, plain yoghurt, natural yoghurt |
| ground beef | minced beef, beef mince |
| canned tomato | tinned tomato |
| canned tuna | tinned tuna |
| vegetable oil | sunflower oil, canola oil, cooking oil |
| olive oil | extra virgin olive oil |
| black pepper | ground black pepper |

### Conversion data (approximate)

Densities are typical kitchen values in grams per millilitre. Item weights are typical weights in grams. They are **approximations** chosen for this project, not exact measurements, and real products differ.

| Ingredient | Grams per ml |
|---|---|
| flour | 0.53 |
| sugar | 0.84 |
| butter | 0.95 |
| salt | 1.2 |
| milk | 1.03 |
| cream | 1.0 |
| yogurt | 1.03 |
| water | 1.0 |
| vegetable stock | 1.0 |
| canned tomato | 1.0 |
| olive oil | 0.91 |
| vegetable oil | 0.92 |
| soy sauce | 1.2 |
| honey | 1.42 |
| mayonnaise | 0.95 |
| oat | 0.34 |
| rice | 0.8 |
| cheese | 0.47 |
| cinnamon | 0.53 |
| curry powder | 0.45 |
| black pepper | 0.46 |
| baking soda | 0.92 |
| cornstarch | 0.53 |

| Ingredient | Grams per item |
|---|---|
| egg | 50 |
| onion | 150 |
| potato | 170 |
| carrot | 60 |
| tomato | 120 |
| banana | 120 |
| lemon | 70 |
| lime | 45 |
| avocado | 150 |
| bell pepper | 150 |
| spring onion | 15 |
| garlic | 5 |
| bread | 30 |
| tortilla | 40 |

For example, 1 kg of sugar satisfies a recipe that needs 1 tablespoon of sugar (15 ml at 0.84 g/ml is 12.6 g), while 12 g does not.

### Seeded recipes

1. Scrambled Eggs on Toast
2. Tomato Pasta
3. Simple Pancakes
4. Cheese Omelette
5. Vegetable Fried Rice
6. Chicken Stir-Fry
7. Lemon Garlic Rice
8. Potato Soup
9. Chickpea Curry
10. Banana Oat Bites
11. Mashed Potatoes
12. Tuna Mayo Sandwich
13. French Toast
14. Simple Guacamole
15. Tomato Soup
16. Honey Yogurt Parfait
17. Cheese Quesadilla
18. Spaghetti Bolognese
19. Egg Fried Noodles
20. Honey Roasted Carrots

## Setup and running

### Requirements

- Android Studio Koala (2024.1.1) or newer. The project uses Android Gradle Plugin 8.5.2.
- The Android SDK, platform 34. Android Studio offers to install it on first sync.
- A JDK 17 or newer. The one bundled with Android Studio is fine.
- An emulator or a phone running Android 8.0 (API 26) or newer.

### Steps

1. Clone the repository: `git clone https://github.com/jody79154/Smart_Pantry_Manager.git`
2. In Android Studio choose **Open** and select the cloned folder. Trust the project if asked.
3. Wait for Gradle sync to finish. The first sync downloads Gradle 8.7 and the libraries, so it needs an internet connection.
4. Pick an emulator (Tools, Device Manager, Create Device) or connect a phone with USB debugging on.
5. Click **Run**.

The repository does not include the Gradle wrapper JAR or the `gradlew` scripts, so build and run from Android Studio, which uses the Gradle version set in `gradle/wrapper/gradle-wrapper.properties`.

## Testing

The project has 85 local unit tests (JUnit 4) in `app/src/test/java`:

| Test class | Covers |
|---|---|
| `RecipeMatcherTest` | the strict-matching rule: full match, a missing ingredient, insufficient quantity, surplus quantity, case, whitespace, singular and plural names, unit conversion, incompatible units, duplicate pantry rows, an empty pantry, multi-ingredient recipes, decimal quantities, aliases, and weight/volume/count conversion |
| `IngredientConversionsTest` | density and item-weight conversion |
| `IngredientNormalizerTest` | plurals, aliases, punctuation, empty input |
| `UnitConverterTest` | unit spellings and conversions |
| `InputValidatorTest` | form validation rules |
| `ExpiryHelperTest` | expiring-soon and expired logic |
| `QuantityFormatterTest` | quantity display |
| `IngredientFormatterTest` | recipe ingredient lines |
| `RecipeCatalogTest` | exactly 20 unique recipes with valid units |

To run them in Android Studio, right-click `app/src/test/java` and choose **Run Tests**.

The database code, the Activities and the layouts are not covered by automated tests and need manual checking:

- [ ] Add, edit and delete an ingredient, then close and reopen the app. The pantry is unchanged.
- [ ] Submit the form with a blank name, a quantity of 0 and the date 2026-02-30. Each shows an error beside its field and nothing is saved.
- [ ] Open Recipes with an empty pantry. The no-match message appears.
- [ ] Add egg 3 item, bread 2 item, butter 10 g and salt 0.5 tsp. Scrambled Eggs on Toast appears. Delete the salt and it disappears.
- [ ] Set eggs to 2 and confirm the recipe is not suggested.
- [ ] Add 1 kg of sugar and check a recipe that needs a tablespoon of it still qualifies.
- [ ] Change the Settings, fully close the app, reopen it, and confirm they were kept.

## Known limitations

- Ingredient names must match as whole names after normalisation. `free range egg` does not match `egg`, and an ingredient or alias that is not in the lists above will not be recognised.
- The density and item-weight tables are approximate and cover only the ingredients listed. For any other ingredient, weight, volume and count amounts are not interchangeable.
- Only the units listed above are supported (no ounces, pounds, pints or pinches).
- Database access runs on the main thread. The data set is small, so this is not noticeable, but it would not scale.
- `onUpgrade` drops and recreates all tables, so a future schema version would erase pantry data.
- A delete confirmation dialog closes if the screen is rotated and has to be opened again.
- There are no notifications; expiring-soon warnings appear only inside the app.
- There is no "Almost There" list, as the project keeps strictly to full matches.
- There are no instrumented (on-device) tests.

## Repository

https://github.com/jody79154/Smart_Pantry_Manager
