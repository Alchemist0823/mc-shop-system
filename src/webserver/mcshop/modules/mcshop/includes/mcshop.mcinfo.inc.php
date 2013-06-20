<?php
	class MCInfo
	{
		var $lasttime;		//time
		var $online;		//bool
		var $player_num;	//int
		var $onlineplayers; //array
		var $plugins; 		//array
	
		public function __construct()
		{
			$this->lasttime = time();
			$this->online = false;
			$this->player_num = 0;
			$this->plugins = array();
			$this->onlineplayers = array();
		}
		
		/**
		 * @return string the display content
		 */
		public function display()
		{
			if($this->online)
				return '<strong>Server is online</strong><ul><li>'.date('jS \of F Y h:i:s A', $this->lasttime).'</li><li>'.$this->player_num.'</li><li>'.$this->plugins.'</li><li>'.$this->onlineplayers.'</li></ul>';
			else
				return '<strong>Server is offline</strong><br/>'.date('jS \of F Y h:i:s A', $this->lasttime);
		}

		/**
		 * @return bool Is need get information from game server.
		 */
		public function isNeedUpdate()
		{
			if(time() - $this->lasttime > 60 * 5)
				return true;
			else
				return false;
		}
		
		/**
		 * @param bool $status
		 */
		public function update($status)
		{
			$this->online = $status;
			$this->lasttime = time();
		}
		

		/**
		 * @return bool Is online.
		 */
		public function isOnline()
		{
			return $this->online;
		}
	}

?>