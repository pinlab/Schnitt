!include LogicLib.nsh

; The name of the installer
Name "JRE_tester"
InstallDir $Desktop  
OutFile "jre-tester.exe"

RequestExecutionLevel user

Var /GLOBAL JAVA_HOME

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
