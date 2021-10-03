count=0
for file in $1/*.txt
do
	c=$(wc -l $file | awk '{ print $1 }')
	count=$((count + c))
done
echo $count
