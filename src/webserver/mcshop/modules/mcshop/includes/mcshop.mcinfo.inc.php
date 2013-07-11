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
			      theme('item_list', array('items' => $this->plugins)),
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
			if(time() - $this->lasttime > 60 * 5)
				return true;
			else
				return false;
		}
		
		/**
		 * @param bool $status
		 */
		public function update($status, $info = NULL)
		{
			$this->online = $status;
			$this->lasttime = time();
			
			if(isset($info))
			{
			}
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