# Quick Start - Get Your OAuth Client ID

## Step 1: Generate SHA-1 Certificate Fingerprint

Run this command in your terminal:

```bash
cd C:\Users\Bappa\AndroidStudioProjects\SmartHealth
.\gradlew signingReport
```

Look for the **SHA-1** line under the `debug` variant. Copy that SHA-1 value.

Example output:
```
Variant: debug
Config: debug
Store: C:\Users\Bappa\.android\debug.keystore
Alias: AndroidDebugKey
MD5: XX:XX:XX:...
SHA1: A1:B2:C3:D4:E5:F6:... ← COPY THIS!
SHA-256: XX:XX:XX:...
```

## Step 2: Add SHA-1 to Firebase Console

1. Go to https://console.firebase.google.com/
2. Select project: **smart-health-dc9e4**
3. Click the gear icon ⚙️ → **Project settings**
4. Scroll down to **Your apps** section
5. Find your Android app
6. Click **Add fingerprint**
7. Paste your SHA-1 and click **Save**

## Step 3: Enable Google Sign-In

1. In Firebase Console, go to **Authentication** (left sidebar)
2. Click **Sign-in method** tab
3. Find **Google** in the list
4. Click **Enable**
5. Add your support email
6. Click **Save**

## Step 4: Download Updated google-services.json

1. Back in **Project settings**
2. Scroll to **Your apps** section
3. Click **google-services.json** download button
4. Replace the file at: `app/google-services.json`

## Step 5: Extract OAuth Client ID

1. Open the NEW `google-services.json` file you just downloaded
2. Look for the `oauth_client` array
3. Find the entry with `"client_type": 3` (Web client)
4. Copy the `client_id` value

Example:
```json
"oauth_client": [
  {
    "client_id": "123456789-abcdefg.apps.googleusercontent.com",
    "client_type": 3
  }
]
```

## Step 6: Update strings.xml

1. Open `app/src/main/res/values/strings.xml`
2. Replace `YOUR_OAUTH_CLIENT_ID_HERE` with the client_id you copied
3. Save the file

Example:
```xml
<string name="default_web_client_id">123456789-abcdefg.apps.googleusercontent.com</string>
```

## Step 7: Test

1. Build and run the app
2. Click "Continue with Google"
3. Select your Google account
4. You should be signed in successfully! 🎉

## Troubleshooting

**Error: "Developer error"**
- SHA-1 not configured correctly
- Make sure you added the correct SHA-1 to Firebase Console
- Download the updated google-services.json

**Error: "Please configure OAuth Client ID"**
- You haven't updated strings.xml yet
- Follow Step 6 above

**Sign-in dialog doesn't show**
- Check that Google Sign-In is enabled in Firebase Console
- Make sure you're using the updated google-services.json

## Need Help?

See the full guide: [FIREBASE_SETUP.md](FIREBASE_SETUP.md)
