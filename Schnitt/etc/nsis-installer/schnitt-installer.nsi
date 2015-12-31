!include LogicLib.nsh

Var /GLOBAL JAVA_HOME

; The name of the installer
Name "Schnitt"
InstallDir $Temp\Schnitt
OutFile "Schnitt.exe"

RequestExecutionLevel user

Section "Copy jars" CopyJars
  SetOutPath $Temp\Schnitt
  File /r jar
  File run.bat
  SectionEnd


Section "Locating JRE Home" LocateJavaHome
  SetRegView 64
  ReadRegStr $0 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" "CurrentVersion"
  ReadRegStr $1 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment\$0" "JavaHome" 
  MessageBox MB_OK 'Java(64view 32bit) ="$1"'
  ${If} $1 != ''
     StrCpy $JAVA_HOME $1
     SetRegView 32
  ${Else}
    ReadRegStr $0 HKLM "SOFTWARE\Wow6432Node\JavaSoft\Java Runtime Environment" "CurrentVersion"
    ReadRegStr $1 HKLM "SOFTWARE\Wow6432Node\JavaSoft\Java Runtime Environment\$0" "JavaHome"
    MessageBox MB_OK 'Java(64view 64bit) ="$1"'
    ${If} $1 != ''
       StrCpy $JAVA_HOME $1
       SetRegView 32
    ${Else}
      SetRegView 32
      ReadRegStr $0 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" "CurrentVersion"
      ReadRegStr $1 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment\$0" "JavaHome" 
      MessageBox MB_OK 'Java(32view 32bit) ="$1"'
      ${If} $1 != ''
         StrCpy $JAVA_HOME $1
      ${Else}
        ReadRegStr $0 HKLM "SOFTWARE\Wow6432Node\JavaSoft\Java Runtime Environment" "CurrentVersion"
        ReadRegStr $1 HKLM "SOFTWARE\Wow6432Node\JavaSoft\Java Runtime Environment\$0" "JavaHome"
        MessageBox MB_OK 'Java(32view 64bit) ="$1"'
        ${If} $1 != ''
           StrCpy $JAVA_HOME $1
        ${EndIf}
      ${EndIf}
    ${EndIf}
  ${EndIf}
SectionEnd

Section "End"
;  MessageBox MB_OK '"$JAVA_HOME\bin\javaw.exe  -cp %TEMP%\Schnitt\jar\* info.pinlab.schnitt.Main"'
;  Exec '"$JAVA_HOME\bin\java.exe" -cp "$INSTDIR\\jar\*" info.pinlab.schnitt.Main'
;  Exec '"$JAVA_HOME\bin\java.exe" "-cp $INSTDIR\\jar\*" info.pinlab.schnitt.Main'
  Exec '"$JAVA_HOME\bin\javaw.exe" "-cp" "$INSTDIR\\jar\*" "info.pinlab.schnitt.Main"'
  Quit
SectionEnd
