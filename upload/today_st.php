<?php
	//勾搭统计日安装app数,日注册用户数,日打开app数
	//正式环境
	$production  = 0;
	//测试环境
	$testing     = 1;
	//开发环境
	$development = 2;

	$environment = 1;

	global 		$_dir;

	global      $mysqli;
	
	switch($environment)
	{
	    case $production:
			$mysqli = new mysqli("192.168.1.13", "root", "", "itopic");    	
	        $_dir = "/var/www/itopic/Application/Api/";
	        break;
	    case $testing:
			$mysqli = new mysqli("192.168.10.235", "root", "mysql", "itopic");
		    $_dir = "/var/www/itopic/Application/Api/";
	        break;
	    case $development:
			$mysqli = new mysqli("localhost", "root", "lebadev209", "itopic");
	        $_dir = "/var/html/itopic/Application/Api/";
	        break;
	}
    /**
    * 递归创建目录
    * @access protected
    * @param string $dir 目录
    * @return bool
    */
    function mkdirs($dir)
    {
        if(!is_dir($dir))
        {
            if(!$this->mkdirs(dirname($dir)))
            {
                return false;
            }
            if(!mkdir($dir,0777))
            {
                return false;
            }
        }
        return true;
    }
    /** 
    * 打印日志
    * @access protected
    * @param string $eventName 文件名
    * @param string $content   打印内容
    */             
    function _log($eventName,$content)
    {   

        $logdir =$GLOBALS['_dir']."log/".date('Y')."/".date('m')."/";
        if(!is_dir($logdir))
        {
            $this->mkdirs($logdir);
        }
        file_put_contents($logdir.date('Y-m-d')."_".$eventName,"[".date('H:i:s')."]".$content."\n", FILE_APPEND);    
    }

 	function st($t)
 	{
 		$mysqli = $GLOBALS['mysqli'];
		if ($mysqli->connect_errno) 
		{
		    _log("st.log","[".date('Y-m-d H:i:s')."]Failed to connect to MySQL: (" . $mysqli->connect_errno . ") " . $mysqli->connect_error);
		    exit;
		}
		$today      = strtotime($t);
		
		$yesterday  = $today-86400;
		
		$date_yesterday    = date('Y-m-d',$yesterday);

		$date_today = date('Y-m-d',$today);

		$install   = $mysqli->query("SELECT count(1) total FROM app_infos where last_modify > $yesterday and last_modify < $today");

		$register  = $mysqli->query("SELECT count(1) total FROM user where created_at >= '$date_yesterday' and  created_at < '$date_today' and `hidden`=0");

		$open      = $mysqli->query("SELECT count(distinct dev_id) total FROM app_open_events where create_time > $yesterday and create_time < $today");
		
		$registers = $mysqli->query("SELECT count(1) total_register_counter from user where `hidden`=0");
		
		$installs = $mysqli->query("SELECT count(1) total_install_counter from app_infos");
		
		$installCount   = $install->fetch_assoc();
		
		$registerCount  = $register->fetch_assoc();
		
		$openCount      = $open->fetch_assoc();

		$installTotal['total_install_counter']   = 0;
		
		$registerTotal['total_register_counter'] = 0;
		
		if($registers)
			$registerTotal = $registers->fetch_assoc();
		
		if($installs)
			$installTotal = $installs->fetch_assoc();
		
		$sql="insert into data_dailies(date,install_counter,register_counter,open_counter,total_register_counter,total_install_counter)values(?,?,?,?,?,?)";

		$stmt=$mysqli->prepare($sql);

		$_opencount = isset($openCount['total'])?$openCount['total']:0;
		
		$stmt->bind_param("siiiii",$date_today,$installCount['total'],$registerCount['total'],$_opencount,$registerTotal['total_register_counter'],$installTotal['total_install_counter']);

		$stmt->execute();

		//echo "最后ID：".$stmt->insert_id."\n";

	    //echo "影响行数：".$stmt->affected_rows."\n";

	 	_log('st.log',"[".date('Y-m-d H:i:s')."]最后ID：".$stmt->insert_id);

	 	_log('st.log',"[".date('Y-m-d H:i:s')."]影响行数：".$stmt->affected_rows);

	 	$stmt->close();

	 	$mysqli->close();
 	}
	$arg = getopt('t:');
 	st($arg['t']);
