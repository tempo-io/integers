gant -f ant/build.gant
rsync -ua ./build/java/ ./dev/
svn diff `svn st ./dev | sed 's/^. *//g' | sed 's/^.*\(IntLong\|LongLong\|IntInt\).*$//g' | sed '/Long/!d'` > ~/trash/integers.diff
svn revert ./dev --depth=infinity
