# Practice Problems

Runnable Java project for end-of-day practice problems and code examples.

## Structure

```
practice-problems/
├── build.gradle          ← Gradle project config (Java 17)
├── settings.gradle
└── src/
    └── main/
        └── java/
            └── com/learn/lld/
                ├── day01/    ← Practice code for Day 01
                ├── day02/    ← Practice code for Day 02
                └── ...
```

## How to Use

```bash
cd practice-problems
./gradlew build        # Compile all code
./gradlew run          # Run main class (if configured)
```

## Connection to Daily Learnings

Each `daily-learnings/day-XX.md` cheat sheet references the corresponding practice code here.
The `.md` files explain the concepts; this folder has the runnable implementations.
