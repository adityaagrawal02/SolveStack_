$javafxDir = "C:\Users\DESHNA\.m2\repository\org\openjfx"
$javafxVersion = "21.0.3"

# Find all JavaFX jars
$jars = Get-ChildItem -Path $javafxDir -Filter "*.jar" -Recurse | Where-Object { $_.FullName -match $javafxVersion -and $_.Name -match "win" }
$modulePath = ($jars | Select-Object -ExpandProperty FullName) -join ";"

# Compile
if (-not (Test-Path "out_run")) { New-Item -ItemType Directory -Path "out_run" }
javac --module-path $modulePath --add-modules javafx.controls -d out_run (Get-ChildItem -Path src -Filter *.java -Recurse | Select-Object -ExpandProperty FullName)

if ($LASTEXITCODE -eq 0) {
    Write-Host "Compilation successful. Running..."
    java --module-path $modulePath --add-modules javafx.controls -cp "out_run;src" main.SolveStackApp
} else {
    Write-Host "Compilation failed."
}
