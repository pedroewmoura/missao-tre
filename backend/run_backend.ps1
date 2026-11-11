$javaExe = "C:\Users\099087980876\.jdks\ms-21.0.12.1\bin\java.exe"
$cp = Get-Content (Join-Path $PSScriptRoot "classpath.txt")
Set-Location $PSScriptRoot
& $javaExe "-Dfile.encoding=UTF-8" -cp $cp missao.demo.DemoApplication
