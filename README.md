Branch README — Transfer (simple)

About this branch

This branch adds a small in-browser chat and a smarter auto-stop so the app quits when idle to save battery. The chat is temporary and kept only while the app runs—no login, no storage.

What's new

- Tiny in-browser chat (no account, stored in memory only)
- Server auto-stops after being idle to save battery
- Small web UI improvements (chat button, unread badge)

How to try

1. (Build and) install the debug APK.
2. Start the app and open the served web page from another device on the same network.
3. Click the chat button and send messages.
4. Leave it idle and the server should stop after ~5 minutes.

Notes

- Messages are ephemeral and not saved to disk.