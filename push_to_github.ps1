# PowerShell script to push code to GitHub
# Run this AFTER creating the repository on GitHub

# Replace REPO_NAME with your actual repository name
$REPO_NAME = "Countdown1356"
$USERNAME = "23f1002381"

Write-Host "Setting up GitHub remote..." -ForegroundColor Green

# Add remote (HTTPS)
git remote add origin "https://github.com/$USERNAME/$REPO_NAME.git"

# Rename branch to main
git branch -M main

Write-Host "Ready to push! Run this command:" -ForegroundColor Yellow
Write-Host "git push -u origin main" -ForegroundColor Cyan
Write-Host ""
Write-Host "Note: You'll need to authenticate with GitHub." -ForegroundColor Yellow
Write-Host "Use your Personal Access Token as the password." -ForegroundColor Yellow

