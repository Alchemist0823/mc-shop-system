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
			$rows = array();
			if($this->online)
			{
			  $rows[] = array(
			      t('Server Status'),
			      '<strong>'.t('Online').'</strong>',
			  );
			  $rows[] = array(
			      t('Server Address'),
			      variable_get_value('mcshop_server_host'),
			  );
			  $rows[] = array(
			      t('Server Time'),
			      format_date($this->lasttime, 'short'),
			  );
			  $rows[] = array(
			      t('Number of Player'),
			      ''.$this->player_num,
			  );
			  $rows[] = array(
			      t('Plugins'),
			      ''.count($this->plugins),
			      //theme('item_list', array('items' => $this->plugins)),
			  );/*
			  $rows[] = array(
			      t('Number of Player'),
			      theme('item_list', array('items' => $this->onlineplayers)),
			  );*/
			}
			else
			{
			  $rows[] = array(
			      t('Server Status'),
			      '<strong>'.t('Offline').'</strong>',
			  );
			  $rows[] = array(
			      t('Server Address'),
			      variable_get_value('mcshop_server_host'),
			  );
			  $rows[] = array(
			      t('Server Time'),
			      format_date($this->lasttime, 'short'),
			  );
			}
			return array(
			    '#theme' => 'table',
			    '#rows' => $rows,
			);
		}

		/**
		 * @return bool Is need get information from game server.
		 */
		public function isNeedUpdate()
		{
			if(time() - $this->lasttime > 60 * 1)
				return true;
			else
				return false;
		}
		
		/**
		 * @param bool $status
		 */
		public function update()
		{
		  $connector = new MCConnector();
		  
		  if($connector->connect()) {
		    $this->online = TRUE;
		    
		    $result = $connector->getServerStatus();
		    
		    $vars = explode(',', $result);
		    foreach ($vars as $var)
		    {
		      $name = strstr($var, ':', true);
		      $value = substr(strstr($var, ':'), 1);
		      if($name === false)
		        continue;
		      if($name ==  'OnlinePlayersNumber')
		        $this->player_num = $value;
		      else if($name ==  'OnlinePlayers')
		      {
		        $this->onlineplayers = explode('|', $value);
		      }
		      else if($name ==  'PluginsNumber')
		      {
		      }
		      else if($name ==  'Plugins')
		      {
		        $this->plugins = explode('|', $value);
		      }
		    }
		    
		    $connector->disconnect();
		  }
		  else
		  {
		    $this->online = FALSE;
		    $this->player_num = 0;
		    $this->onlineplayers = array();
		    $this->plugins = array();
		  }
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