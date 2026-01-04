# GitHub Repository Setup Instructions

## Step 1: Create Repository on GitHub

1. Go to https://github.com/new
2. Sign in with your GitHub account (username: 23f1002381)
3. Fill in the repository details:
   - **Repository name**: `Countdown1356` (or any name you prefer)
   - **Description**: "Android app for 1356-day countdown timer"
   - **Visibility**: Choose Public or Private
   - **DO NOT** initialize with README, .gitignore, or license (we already have these)
4. Click **"Create repository"**

## Step 2: Push Your Code

After creating the repository, GitHub will show you commands. Use these instead:

```bash
# Add the remote repository (replace YOUR_USERNAME with 23f1002381 and REPO_NAME with your repo name)
git remote add origin https://github.com/23f1002381/Countdown1356.git

# Rename branch to main (if needed)
git branch -M main

# Push your code
git push -u origin main
```

## Alternative: Using SSH (if you have SSH keys set up)

```bash
git remote add origin git@github.com:23f1002381/Countdown1356.git
git branch -M main
git push -u origin main
```

## Authentication

When you push, GitHub will ask for authentication:
- **Username**: 23f1002381
- **Password**: Use a Personal Access Token (not your GitHub password)
  - Go to: https://github.com/settings/tokens
  - Generate new token (classic)
  - Select scopes: `repo` (full control)
  - Copy the token and use it as password

---

**Quick Command Summary** (run these after creating the repo on GitHub):

```bash
cd C:\Users\someone\Desktop\Project1356
git remote add origin https://github.com/23f1002381/Countdown1356.git
git branch -M main
git push -u origin main
```

