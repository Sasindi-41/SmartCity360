[CmdletBinding()]
param(
    [switch]$SkipPackage
)

$ErrorActionPreference = 'Stop'

$projectRoot = Split-Path -Parent $PSScriptRoot
$pomPath = Join-Path $projectRoot 'pom.xml'
$evidenceRoot = Join-Path $projectRoot 'test-evidence'
$reportsRoot = Join-Path $evidenceRoot 'surefire-reports'

if (-not (Test-Path $pomPath)) {
    throw "pom.xml was not found. Run this script from the project's scripts folder or keep the script inside the project."
}

if (-not (Get-Command mvn -ErrorAction SilentlyContinue)) {
    throw "Maven was not found on PATH. Install Maven 3.8+ and verify that 'mvn -version' works, then run this script again."
}

New-Item -ItemType Directory -Force -Path $evidenceRoot | Out-Null
Set-Location $projectRoot

$testLog = Join-Path $evidenceRoot 'unit-tests-console.txt'
$packageLog = Join-Path $evidenceRoot 'package-console.txt'

Write-Host "Project: $projectRoot"
Write-Host 'Running: mvn clean test'

& mvn clean test 2>&1 | Tee-Object -FilePath $testLog
$testExitCode = $LASTEXITCODE

if (Test-Path (Join-Path $projectRoot 'target/surefire-reports')) {
    New-Item -ItemType Directory -Force -Path $reportsRoot | Out-Null
    Copy-Item -Path (Join-Path $projectRoot 'target/surefire-reports/*') -Destination $reportsRoot -Recurse -Force
}

if ($testExitCode -ne 0) {
    Write-Error "Unit tests failed (Maven exit code $testExitCode). Review $testLog and $reportsRoot. Packaging was not attempted."
    exit $testExitCode
}

Write-Host 'Unit tests completed successfully.'

if (-not $SkipPackage) {
    Write-Host 'Running: mvn clean package'
    & mvn clean package 2>&1 | Tee-Object -FilePath $packageLog
    $packageExitCode = $LASTEXITCODE

    if (Test-Path (Join-Path $projectRoot 'target/surefire-reports')) {
        $finalReportsRoot = Join-Path $evidenceRoot 'package-surefire-reports'
        New-Item -ItemType Directory -Force -Path $finalReportsRoot | Out-Null
        Copy-Item -Path (Join-Path $projectRoot 'target/surefire-reports/*') -Destination $finalReportsRoot -Recurse -Force
    }

    if ($packageExitCode -ne 0) {
        Write-Error "Maven packaging failed (exit code $packageExitCode). Review $packageLog."
        exit $packageExitCode
    }

    $warPath = Join-Path $projectRoot 'target/SmartCity360.war'
    if (-not (Test-Path $warPath)) {
        Write-Error "Maven returned success but the expected WAR was not found at $warPath."
        exit 1
    }

    Write-Host "WAR created: $warPath"
}

Write-Host "Evidence saved under: $evidenceRoot"
Write-Host 'This script does not connect to MySQL, start Payara, or modify application/database records.'
