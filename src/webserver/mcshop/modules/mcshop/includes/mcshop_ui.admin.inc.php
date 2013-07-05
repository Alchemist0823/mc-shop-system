<?php
//6.29 - the list of players online OR the status details of playerselected

//IMPORTANT: haven't been tested yet...

//the argument to receive the playername selected, '' means to show the list
//e.g. 'admin/reports/mcshop' calls the function with $playerselected==''
//     'admin/reports/mcshop/name' calls the function with $playerselected=='name'
//if the above works, hook_menu needn't be changed.

function _mcshop_admin_instructions($playerselected = '')
{
	$base_content = t('This is the base page of MC Admin. Enjoy!');
	$result = '<p>'.$base_content.'</p>';
	$mcinfo = variable_get('mcshop_mcinfo');

	if($mcinfo == NULL)
	{
		$mcinfo = new MCInfo();
		variable_set('mcshop_mcinfo', $mcinfo);
	}
	
	if($mcinfo->isNeedUpdate())
	{
		$connector = new MCConnector();
		if($connector->connect()) {
			$success = true;//$connector->getServerStatus();
			$connector->disconnect();
			if($success)
				$mcinfo->update(true);
			else
				$mcinfo->update(false);
		}
		else
			$mcinfo->update(false);
		variable_set('mcshop_mcinfo', $mcinfo);
	}

	
	if($playerselected == '') {      //display the list
		$result=$result.'<div><ul>';
		if ($mcinfo->isOnline()) {
			foreach($mcinfo->onlineplayers as $playername) {
				$result = $result.'<li><a href="'.$playername.'" target="_blank">'.$playername.'</a></li>';
			}
		}
		else {
			$result = $result.'Server is offline.';
		}
		$result = $result.'</ul></div>';
	}
	else {       //display the status of one player
		$result=$result.'<div>';
		if ($mcinfo->isOnline()){
			$connector = new MCConnector();
			if($connector->connect()){
				$playerstatus = $connector->getPlayerStatus($playerselected);
				$result = $result.$playerstatus;
				$connector->disconnect();
			}
			else
				$result = $result.'Cannot connect to server.';
		}
		else{
			$result = $result.'Server is offline.';
		}
		$result = $result.'</div>';
	}
	
	return $result;
}
?>
