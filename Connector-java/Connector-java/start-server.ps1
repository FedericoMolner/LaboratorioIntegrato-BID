#!/usr/bin/env pwsh
# Start server script: start-server.ps1
# Usage: .\start-server.ps1 -JarPath path\to\jar -WaitSeconds 30

param(
    [string]$JarPath = "target\Connector-java-0.0.1-SNAPSHOT.jar",
    [int]$WaitSeconds = 30
)

if (-not (Test-Path $JarPath)) {
    Write-Error "Jar not found: $JarPath"; exit 1
}

$outLog = "server_out.log"
$errLog = "server_err.log"
if (Test-Path $outLog) { Remove-Item $outLog -Force }
if (Test-Path $errLog) { Remove-Item $errLog -Force }

Start-Process -FilePath 'java' -ArgumentList '-jar', $JarPath -RedirectStandardOutput $outLog -RedirectStandardError $errLog -NoNewWindow

Write-Host "Launched jar in background; waiting for startup marker (max $WaitSeconds seconds) ..."
$end = (Get-Date).AddSeconds($WaitSeconds)
while ((Get-Date) -lt $end) {
    if (Test-Path $outLog) {
        $content = Get-Content $outLog -Tail 50 -ErrorAction SilentlyContinue | Out-String
        if ($content -match "Started ConnectorJavaApplication") {
            Write-Host 'Server started successfully.'; exit 0
        }
    }
    Start-Sleep -Seconds 1
}
Write-Error "Server did not start within $WaitSeconds seconds; check $errLog and $outLog"; exit 2
