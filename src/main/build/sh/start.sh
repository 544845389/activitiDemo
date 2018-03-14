#!/bin/sh
file_name_tmp=`ls ./`;
file_names=($(echo $file_name_tmp));
jarName='';
for file_name in ${file_names[@]}
do
if [ "${file_name##*.}"x = "jar"x ];then
   jarName=$file_name;
   break
fi
done

if [ "$jarName"x != ""x ];then
	pid=`ps -ef | grep $jarName | grep -v grep | awk '{print $2}'`

	if [ -n "$pid" ]
	then
	   #!kill -9 强制终止
	   echo "kill -9 的pid: $pid"
	   kill -9 $pid
	fi

	. /etc/profile
	nohup java -jar *.jar -server -Xms128m -Xmx256m -XX:MaxPermSize=128m  > run.log 2>&1 &
	echo ' start >>>>>>SUCCESS !!!!!'

else
	echo 'jar null !!!';
fi
