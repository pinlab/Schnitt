import sys
import os.path

schnitt_jar = "WavPanel-0.1.jar" 
schnitt_jar = "../../target/" + schnitt_jar
schnitt_jar_abs_path = os.path.abspath(schnitt_jar)
schnitt_jy_abs_path = os.path.abspath(sys.argv[0])


if not os.path.isfile(schnitt_jar_abs_path): 
    sys.stderr.write("\n[ERR]  No Schnitt jar is at '" + schnitt_jar_abs_path +"'")
    sys.stderr.write("\n[ERR]  Solution: step 1) create uber jar for project Schnitt")
    sys.stderr.write("\n[ERR]            step 2) change path in script '" + schnitt_jy_abs_path +"'\n\n")
    sys.exit()

sys.path.append(schnitt_jar_abs_path)


# import logger as LOG
import org.slf4j.Logger
LOG = org.slf4j.LoggerFactory.getLogger("schnitt.jy")


