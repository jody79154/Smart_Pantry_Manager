# Smart Pantry Manager

An Android app that helps you cook with what you already have. Track the ingredients in your pantry, and the app tells you — strictly and honestly — which recipes you can actually make right now, with no shopping trip required.

Repository: https://github.com/jody79154/Smart_Pantry_Manager

## Problem and target user

Food waste often comes down to a simple gap: people forget what's in their pantry and either buy duplicates or let ingredients expire unused. Smart Pantry Manager is for anyone who wants to reduce that waste and get quick, realistic meal ideas from what's already on their shelf — not a recipe browser that assumes you'll go shopping first.

## Main features

- **Pantry management** — add, edit, and delete ingredients (name, quantity, unit, optional expiry date), with full validation and a deletion confirmation dialog.
- **Persistent storage** — everything survives closing and reopening the app.
- **Suggested Recipes** — runs a strict-matching algorithm against your current pantry and shows only recipes you can make in full, right now.
- **Recipe Detail** — full ingredient list and method for any suggested recipe.
- **Settings** — toggle expiring-soon alerts (shown on pantry rows) and a metric-units preference, both persisted.
- **20 seeded recipes** covering successful matches, missing-ingredient cases, insufficient-quantity cases, unit conversions, and a genuine zero-match state.

## Technology stack

- **Java** (no Kotlin), Android Studio, XML layouts, AndroidX
- **SQLite** via `SQLiteOpenHelper` for persistence
- **RecyclerView** with custom adapters for the pantry list, recipe suggestions, and recipe ingredient list
- **Explicit Intents** for navigation and passing database IDs between screens
- **Material Components** for UI (bottom navigation, text fields, switches)
- No mapping SDKs, location services, or payment processing — the app's scope is strictly the user's own pantry.

## Why SQLite

The app's data is simple, relational, and entirely local to one device — there's no need for network sync or a backend. `SQLiteOpenHelper` gives direct control over schema, foreign keys, and query performance without the overhead of a cloud service, and it's well suited to a small, well-defined relational structure like this one (pantry items, recipes, and each recipe's required ingredients).

## Database structure

Three tables:

```
pantry_items
  id              INTEGER PRIMARY KEY AUTOINCREMENT
  name            TEXT NOT NULL
  quantity        REAL NOT NULL
  unit            TEXT NOT NULL
  expiry_date     TEXT            -- nullable

recipes
  id              INTEGER PRIMARY KEY AUTOINCREMENT
  name            TEXT NOT NULL UNIQUE
  steps           TEXT NOT NULL

recipe_ingredients
  id                  INTEGER PRIMARY KEY AUTOINCREMENT
  recipe_id           INTEGER NOT NULL  -- FK -> recipes(id), ON DELETE CASCADE
  ingredient_name     TEXT NOT NULL
  quantity            REAL NOT NULL
  unit                TEXT NOT NULL
```

Foreign keys are enabled via `onConfigure()`. The 20 recipes are seeded once, inside a transaction, from `onCreate()` — since `onCreate()` only runs the first time the database file is created, reopening the app never duplicates them.

Settings (expiry alerts, metric-units preference) are stored in `SharedPreferences` rather than a fourth table, since they're simple boolean flags with no relational structure.

## Strict-matching explanation

A recipe is only suggested if **every** required ingredient is present in the pantry, in a compatible unit, in at least the required quantity. One missing, incompatible, or insufficient ingredient excludes the whole recipe — there are no partial or "almost there" results in the main list.

The matcher (`logic/RecipeMatcher.java`) works in three stages:

1. **Normalize** each ingredient name (`logic/IngredientNormalizer.java`) — trim, lowercase, apply a small documented alias table (e.g. `capsicum` → `bell pepper`), and naively singularize plurals (`tomatoes` → `tomato`).
2. **Convert** each quantity into its unit category's base unit (`logic/UnitConverter.java`) — grams for mass, millilitres for volume, item-count for count-based ingredients. Units from different categories (e.g. grams vs. millilitres) are never treated as compatible.
3. **Combine and compare** — pantry quantities are grouped by `normalized name + unit category` using `BigDecimal` totals (so, for example, two separate pantry entries for "onion" are summed together), then every recipe requirement is checked against that combined total.

If no recipe qualifies, the Suggestions screen shows: *"No recipes match your pantry yet—add more ingredients."*

Known limitation: the singularizer is a simple suffix-based rule, not a dictionary — irregular plurals (e.g. "leaves" → "leaf") aren't handled.

## Setup instructions

1. Install Android Studio (Giraffe or later recommended) with an Android SDK for API 34.
2. Clone the repository:
   ```
   git clone https://github.com/jody79154/Smart_Pantry_Manager.git
   ```
3. Open the project folder in Android Studio and let Gradle sync.

## Build and run instructions

- **From Android Studio:** open the project, select a device or emulator (minimum API 24), and press Run.
- **From the command line:**
  ```
  ./gradlew assembleDebug
  ./gradlew installDebug
  ```

## Testing instructions

Unit tests for the strict-matching logic live in `app/src/test/java/com/jody/smartpantry/logic/RecipeMatcherTest.java` and run on the local JVM (no emulator required):

```
./gradlew test
```

They cover exact matches, missing ingredients, insufficient and surplus quantities, case/whitespace/plural differences, compatible unit conversion, incompatible units, an unrecognised unit, duplicate pantry entries combining to become sufficient, an empty pantry, a multi-ingredient recipe requiring every ingredient, and decimal quantities.

## Known limitations

- The singularizer handles common English plural patterns but not irregular ones.
- Unit conversion covers mass (g/kg), volume (ml/l/tsp/tbsp), and count (item/piece/whole); it does not attempt density-based conversion between mass and volume.
- Database schema upgrades currently drop and recreate all tables rather than migrating data — acceptable for this project's scope, but not production-grade.
- No cloud sync or multi-device support; data is local to the device.

## References

- Android developer documentation: https://developer.android.com/docs
- `SQLiteOpenHelper` reference: https://developer.android.com/reference/android/database/sqlite/SQLiteOpenHelper
- Material Components for Android: https://github.com/material-components/material-components-android
