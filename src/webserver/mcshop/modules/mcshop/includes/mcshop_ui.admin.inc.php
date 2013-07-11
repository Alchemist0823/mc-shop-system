<?php
//6.29 - the list of players online OR the status details of playerselected
//7.11 - Update: detailed information displayed in table

//the argument to receive the playername selected, '' means to show the list
//e.g. 'admin/reports/mcshop' calls the function with $playerselected==''
//     'admin/reports/mcshop/name' calls the function with $playerselected=='name'
//if the above works, hook_menu needn't be changed.

function _mcshop_admin_instructions($playerselected = '')
{

	$mcinfo = variable_get('mcshop_mcinfo');

	if($mcinfo == NULL)
	{
		$mcinfo = new MCInfo();
		variable_set('mcshop_mcinfo', $mcinfo);
	}

	if($mcinfo->isNeedUpdate())
	{
	  $mcinfo->update();
	  variable_set('mcshop_mcinfo', $mcinfo);
	}
	
	if($playerselected == '') {      //display the list
		
		$rows = array();
		if($mcinfo->isonline())
		{
		  $rows[] = array(
			  '<h2>'.t('MCShop Admin Report').'</h2>','',
		  );
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
		      format_date($mcinfo->lasttime, 'short'),
		  );

		  $rows[] = array(
		      t('Number of Plugins'),
		      ''.count($mcinfo->plugins),
		      //theme('item_list', array('items' => $mcinfo->plugins)),
		  );
		  $rows[] = array(
			  t('Plugin List'),
		      //''.count($mcinfo->plugins),
		      theme('item_list', array('items' => $mcinfo->plugins)),
		  );
		  $rows[] = array(
		      t('Number of Players'),
		      ''.$mcinfo->player_num,
		  );	

		  $playerlist = $mcinfo->onlineplayers;
		  foreach($playerlist as &$playername)
			 $playername = "<a href='".'mcshop/'.$playername."'>".$playername.'</a>';
		  $rows[] = array(
			  t('Player List (Click to view details)'),
			  
			  theme('item_list', array('items' => $playerlist)),
		  );
		}
		else
		{
		  $rows[] = array(
			  '<h2>'.t('MCShop Admin Report').'</h2>',
		  );
		  $rows[] = array(
		      '<h4>'.t('Server is now offline').'</h4>',
		  );

		}
		
		return array(
		    '#theme' => 'table',
		    '#rows' => $rows,
		);
		
	}
	else {       //display the status of one player

		$rows = array();
		if($mcinfo->isonline())
		{
		
		  $connector = new MCConnector();
		  if($connector->connect()){
			$playerstatus = $connector->getPlayerStatus($playerselected);
			$rows[] = array(
			  '<h2>'.t('Player Details').'</h2>',
			); 
			$vars = explode(',', $playerstatus);
			foreach ($vars as $var)
			{
				$name = strstr($var, ':', true);
				$value = substr(strstr($var, ':'), 1);
				if($name === false)
					continue;
				if($name == 'DisplayName'){
					$rows[] = array(
						t('Player Name'),$value,
					);
				}
				else if($name == 'PlayerTime')
				{
					$rows[] = array(
						t('Player Time'),$value,
					);
				}
				else if($name == 'LastPlayed')
				{
					$rows[] = array(
						t('Last Time Played'),$value,
					);
				}
				else if($name == 'Level')
				{
					$rows[] = array(
						t('Player Level'),$value,
					);
				}
				else if($name == 'Exp')
				{
					$rows[] = array(
						t('Player Experience'),$value,
					);
				}	
				else if($name == 'FoodLevel')
				{
					$rows[] = array(
						t('Player Food Level'),$value,
					);
				}
				else if($name == 'Health')
				{
					$rows[] = array(
						t('Player Health'),$value,
					);
				}				
			}
			$connector->disconnect();
		  }
		  else
		  {
		  	  $rows[] = array(
				'<h2>'.t('Player Details').'</h2>',
			  );
			  $rows[] = array(
				'<h4>'.t('Could not connect to server').'</h4>',
			  );
		  }
		}

		else
		{
		  $rows[] = array(
			  '<h2>'.t('Player Details').'</h2>',
		  );
		  $rows[] = array(
		      '<h4>'.t('Server is now offline').'</h4>',
		  );

		}
		
		return array(
		    '#theme' => 'table',
		    '#rows' => $rows,
		);
		
	}
	
}
?>
