# Print js files by lines of code

cd ./src/cmsc434/funpath
#( find . -name '*.js' -print0 | xargs -0 cat ) | wc -l
find . -name '*.java' -print0 | xargs -0 wc -l | sort
read -p "$*"