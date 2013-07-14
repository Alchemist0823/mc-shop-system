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
		  );
		  $rows[] = array(
			  t('Plugin List'),
		      theme('item_list', array('items' => $mcinfo->plugins)),
		  );
		  $rows[] = array(
		      t('Number of Players'),
		      ''.$mcinfo->player_num,
		  );
		  $playerlist = array();
		  $playerlist = $mcinfo->onlineplayers;
		  foreach($playerlist as &$playername)
			 $playername = "<a href='".'mcshop/'.$playername."' target='_blank'>".$playername.'</a>';
		  $rows[] = array(
			  t('Player List (Click to view details)'),
			  
			  theme('item_list', array('items' => $playerlist)),
		  );
		  return array(
		    '#caption' => '<h2>'.t('MCShop Admin Report').'</h2>',
		    '#theme' => 'table',
		    '#rows' => $rows,
		  );
		}
		else
		{
		  return array(
		      '#markup' => '<h4>'.t('Server is now offline').'</h4>',
		  );
		}
	}
	else {       //display the status of one player

		$rows = array();
		if($mcinfo->isonline())
		{
		
		  $connector = new MCConnector();
		  if($connector->connect()){
			$playerstatus = $connector->getPlayerStatus($playerselected);
			$vars = explode(',', $playerstatus);
			foreach ($vars as $var)
			{
		        $pos = strpos($var, ':');
		        if($pos === false)
		          continue;
		        $name = substr($var, 0, $pos);
		        $value = substr($var, $pos + 1);
				$rows[] = array(
					$name,
				    $value,
				);
			}
			$connector->disconnect();
		  }
		  else
		  {
    		  return array(
    		      '#markup' => '<h4>'.t('Could not connect to server').'</h4>',
    		  );
		  }
		}
		else
		{
		  return array(
		      '#markup' => '<h4>'.t('Server is now offline').'</h4>',
		  );
		}
		
		return array(
		    '#caption' => '<h2>'.t('Player Details').'</h2>',
		    '#theme' => 'table',
		    '#rows' => $rows,
		);
	}
	
}
?>
