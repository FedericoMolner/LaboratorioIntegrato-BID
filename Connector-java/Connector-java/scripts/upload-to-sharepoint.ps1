param(
    [string]$SiteUrl = "https://<your_tenant>.sharepoint.com/sites/<your_site>",
    [string]$Library = "Shared Documents",
    [string]$Folder = "Connector-java",
    [string]$FilePath = "connector-java-shareable.zip"
)

# Ensure PnP.PowerShell module is installed
if ((Get-Module -ListAvailable -Name PnP.PowerShell) -eq $null) {
    Write-Host "PnP.PowerShell module not found. Installing..." -ForegroundColor Yellow
    Install-Module -Name PnP.PowerShell -Scope CurrentUser -Force
}

Write-Host "Please authenticate to SharePoint (interactive)" -ForegroundColor Cyan
Connect-PnPOnline -Url $SiteUrl -Interactive

Write-Host "Uploading file $FilePath to $SiteUrl/$Library/$Folder..." -ForegroundColor Cyan
Add-PnPFile -Path $FilePath -Folder "$Library/$Folder" -Force

Write-Host "File uploaded successfully." -ForegroundColor Green
Write-Host "You can generate a share link by running the following in PowerShell (or via the UI):" -ForegroundColor Yellow
Write-Host "Get-PnPFile -Url \"$Library/$Folder/$FilePath\" -AsListItem | Select -ExpandProperty FileRef"
