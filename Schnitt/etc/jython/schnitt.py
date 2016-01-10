import sys, logging
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

logger = logging.getLogger(__name__)
logger.setLevel(logging.DEBUG)
formatter = logging.Formatter('[%(levelname)s]  %(message)s')

logfilename = sys.argv[0][:-3]+".log"
if os.path.isfile(logfilename): 
    os.remove(logfilename)  # comment out/delet if you need log file
fh = logging.FileHandler(logfilename)
fh.setFormatter(formatter)
fh.setLevel(logging.DEBUG)

ch = logging.StreamHandler()
ch.setFormatter(formatter)
ch.setLevel(logging.INFO)

logger.addHandler(fh)
logger.addHandler(ch)     

