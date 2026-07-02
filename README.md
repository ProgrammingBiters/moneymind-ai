# MoneyMind AI — Module 1: Skeleton

This is the foundation of the app: Clean Architecture layout, Hilt dependency
injection, an **encrypted** Room database (SQLCipher, key sealed in the
Android Keystore), PIN + biometric lock with auto-lock on inactivity, a
Material 3 theme with dynamic color, and the navigation graph tying it
together.

Nothing here talks to the network yet — that starts in Module 3 (SMS/Gmail
parsing). Everything runs fully offline.

## What's actually implemented (not stubbed)

- **Security**: `SecureStore` (Keystore-backed `EncryptedSharedPreferences`),
  `AppLockManager` (PBKDF2-SHA256 PIN hashing, 120k iterations, auto-lock
  timer), `BiometricAuthManager` (fingerprint/face via `BiometricPrompt`).
- **Data**: `AppDatabase` (Room over SQLCipher — the `.db` file on disk is
  fully encrypted; the passphrase is generated once with `SecureRandom` and
  never leaves the Keystore-encrypted store).
- **UI**: PIN setup, PIN entry (with biometric prompt shown automatically),
  a placeholder Home screen, full Material 3 theme with Material You dynamic
  color support.
- **Navigation**: Splash routes to setup / entry / home based on real lock
  state, not a hardcoded flag.

## Getting an APK — you do NOT need Android Studio

This repo includes `.github/workflows/build.yml`. GitHub's servers do the
compiling; you just need a free GitHub account.

1. Create a new repository on [github.com](https://github.com) (e.g.
   `moneymind-ai`). Keep it **private** if you're not comfortable with the
   code being public — that's fine, Actions works on private repos too on
   the free tier.
2. Upload the contents of this folder to that repo. Easiest way if you don't
   have git installed locally: on the repo page, click **"uploading an
   existing file"** and drag in everything (keep the folder structure).
3. Go to the **Actions** tab of your repo. A workflow called
   **"Build MoneyMind AI APK"** should already be queued or running (it
   triggers automatically on push). If not, click it and hit **"Run
   workflow"**.
4. Wait a few minutes. When it finishes with a green check, click into the
   run, scroll to **Artifacts**, and download **`moneymind-debug-apk`** — it's
   a zip containing `app-debug.apk`.
5. Transfer that APK to your Android phone (email it to yourself, use Google
   Drive, USB, whatever) and tap it to install. You'll need to allow "install
   from unknown sources" for whichever app you use to open it — that's normal
   for any APK not from the Play Store.

This debug build is unsigned with a release key (it uses Android's default
debug signing), which is fine for installing on your own device but not for
publishing to the Play Store later.

## If you ever do want to open it in Android Studio

Just download Android Studio (free, from Google), choose "Open", and point
it at this folder. It will sync Gradle and run on a device/emulator with a
single click. You don't need to do this — the GitHub Actions path above is
enough to get a working app on your phone.

## What's next (Module 2)

Real transaction data: manual entry, categories, the dual personal/business
ledger, and a dashboard that shows actual balances instead of a placeholder
screen. Say the word and we'll keep building directly in this same repo.
