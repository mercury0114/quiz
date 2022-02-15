count=0
for file in $(find $1 | grep ".txt")
do
	c=$(wc -l $file | awk '{ print $1 }')
	count=$((count + c))
done
echo $count
