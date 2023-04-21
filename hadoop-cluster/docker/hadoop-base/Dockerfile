FROM centos:7

RUN mkdir -p /opt/jdk && \
	mkdir -p /opt/hadoop && \
	mkdir -p /data/tmp

RUN yum update -y && \
	yum install -y net-tools && \
	yum install -y vim-enhanced && \
	yum install -y wget && \
	yum install -y openssh-server openssh-clients openssh-askpass && \
	yum install -y java-1.8.0-openjdk-devel

RUN ln -s /usr/lib/jvm/java-1.8.0-openjdk-1.8.0.352.b08-2.el7_9.aarch64 /opt/jdk/current

RUN echo -e "export JAVA_HOME=/opt/jdk/current\nexport PATH=\$PATH:\$JAVA_HOME/bin" >> /etc/profile

ENV JAVA_HOME=/opt/jdk/current \
	PATH=$PATH:$PATH:$JAVA_HOME/bin

WORKDIR /opt/hadoop

RUN wget https://archive.apache.org/dist/hadoop/common/hadoop-3.1.3/hadoop-3.1.3.tar.gz && \
	tar xvzf hadoop-3.1.3.tar.gz
	
RUN ln -s /opt/hadoop/hadoop-3.1.3 /opt/hadoop/current

RUN echo -e "export HADOOP_HOME=/opt/hadoop/current\nexport PATH=\$PATH:\$HADOOP_HOME/bin\nexport PATH=\$PATH:\$HADOOP_HOME/sbin" >> /etc/profile

ENV HADOOP_HOME=/opt/hadoop/current \
	PATH=$PATH:$HADOOP_HOME/bin \
	PATH=$PATH:$HADOOP_HOME/sbin

RUN ssh-keygen -f /etc/ssh/ssh_host_rsa_key -t rsa -N "" && \
	ssh-keygen -f /etc/ssh/ssh_host_ecdsa_key -t ecdsa -N "" && \
	ssh-keygen -f /etc/ssh/ssh_host_ed25519_key -t ed25519 -N ""

RUN echo "/usr/sbin/sshd" >> /etc/profile

RUN source /etc/profile

RUN ssh-keygen -t rsa -P "" -f ~/.ssh/id_rsa

RUN cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys

WORKDIR $HADOOP_HOME/etc/hadoop

RUN echo -e "export HADOOP_PID_DIR=/opt/hadoop/current/pids\nexport JAVA_HOME=/opt/jdk/current\nexport HDFS_NAMENODE_USER=\"root\"\nexport HDFS_DATANODE_USER=\"root\"\nexport HDFS_SECONDARYNAMENODE_USER=\"root\"\nexport YARN_RESOURCEMANAGER_USER=\"root\"\nexport YARN_NODEMANAGER_USER=\"root\"" >> hadoop-env.sh && \
	sed -i "s/<configuration>/<configuration>\n\t<property>\n\t\t<name>fs.defaultFS<\/name>\n\t\t<value>hdfs:\/\/master-1:9000<\/value>\n\t<\/property>/g" core-site.xml && \
	sed -i "s/<configuration>/<configuration>\n\t<property>\n\t\t<name>dfs.replication<\/name>\n\t\t<value>3<\/value>\n\t<\/property>\n\t<property>\n\t\t<name>dfs.permissions<\/name>\n\t\t<value>false<\/value>\n\t<\/property>\n\t<property>\n\t\t<name>dfs.datanode.data.dir<\/name>\n\t\t<value>file:\/opt\/hadoop\/current\/data\/datanode<\/value>\n\t<\/property>/g" hdfs-site.xml && \
	sed -i "s/<configuration>/<configuration>\n\t<!-- Site specific YARN configuration properties -->\n\t<property>\n\t\t<name>yarn.nodemanager.aux-services<\/name>\n\t\t<value>mapreduce_shuffle<\/value>\n\t<\/property>\n\t<property>\n\t\t<name>yarn.resourcemanager.hostname<\/name>\n\t\t<value>master-1<\/value>\n\t<\/property>\n\t<property>\n\t\t<name>yarn.nodemanager.resource.memory-mb<\/name>\n\t\t<value>2048<\/value>\n\t<\/property>\n\t<property>\n\t\t<name>yarn.nodemanager.resource.cpu-vcores<\/name>\n\t\t<value>1<\/value>\n\t<\/property>\n\t<property>\n\t\t<name>yarn.nodemanager.env-whitelist<\/name>\n\t\t<value>JAVA_HOME,HADOOP_COMMON_HOME,HADOOP_HDFS_HOME,HADOOP_CONF_DIR,CLASSPATH_PREPEND_DISTCACHE,HADOOP_YARN_HOME,HADOOP_MAPRED_HOME<\/value>\n\t<\/property>/g" yarn-site.xml && \
	sed -i "s/<configuration>/<configuration>\n\t<property>\n\t\t<name>mapreduce.framework.name<\/name>\n\t\t<value>yarn<\/value>\n\t<\/property>/g" mapred-site.xml

CMD /usr/sbin/sshd
