<?php

/**
 * @file
 * Module file for MC Shop System.
 */
 
/**
 * Implements hook_variable_info().
 * in the Variable module
 */
function mcshop_variable_info($options) {

	$variables['mcshop_server_host'] = array(
		'name' => 'mcshop_server_host',
		'title' => t('MCShop Host Name'),
		'description' => t('Specify MCShop Host Name or IP'),
		'default' => 'localhost',
		'type' => 'string',
		'group' => 'mcshop_settings',
		'token' => False,
	);
	
	$variables['mcshop_server_port'] = array(
		'name' => 'mcshop_server_port',
		'title' => t('MCShop Host Port'),
		'description' => t('Specify MCShop Host Port'),
		'default' => 10808,
		'type' => 'number',
		'group' => 'mcshop_settings',
		'token' => False,
	);
	
	$variables['mcshop_server_pass'] = array(
		'name' => 'mcshop_server_pass',
		'title' => t('MCShop Host Password'),
		'description' => t('Specify MCShop System Password'),
		'default' => '123456',
		'type' => 'string',
		'group' => 'mcshop_settings',
		'token' => False,
	);
	return $variables;
}

/**
 * Implements hook_cron().
 * 
 * Get Information form Minecraft Server periodically 
 */
function mcshop_cron()
{
	
}

/**
 * Function Send Cmd Through MCConnecter
 * @param string $cmd
 * @param string $args
 * @return bool $state
 */
function _mcshop_sendcmd($cmd, $args)
{
  
  $connector = new MCConnector();
  
  if($connector->connect()) {
  	$success = $connector->doCommand($cmd);
  	$connector->disconnect();
  	if($success)
  		return true;
  	else
  		return false;
  }
  else return false;
  // TODO: doCommand
}

/**
 * Implements hook_commerce_checkout_complete().
 * in the Commerce module
 */
function mcshop_commerce_checkout_complete($order) {
  foreach ($order->commerce_line_items['und'] as $line) {
    $line_item = commerce_line_item_load($line['line_item_id']);
    $product_id = $line_item->commerce_product['und'][0]['product_id'];
    $product = commerce_product_load($product_id);
    
    if(isset($product->field_buycmd['und'][0]['value']))
    {
   	  $cmd = $product->field_buycmd['und'][0]['value'];
   	  
   	  global $user;
   	  
   	  $args = array(
		'player' => $user->name,
		'quantity' => (int)$line_item->quantity,
   	  );
		
	  if(_mcshop_sendcmd($cmd,$args))
	  {
	  	//$order->status = 'Completed';
	  	// TODO: change order status Line item status
	  }
    }
	else if($product->sku == 'recharge_mcm')
    {

    	global $user;
    	// Recharge Money
    	$currency = commerce_userpoints_load('MCM');
    	
    	$points = (int)($line_item->commerce_total['und'][0]['amount'] / 100 / $currency['conversion_rate']);
    	$params = array(
    			'points' => $points,
    			'uid' => $user->uid,
    			'operation' => $user->name.' Recharge '.$points,
    	);
    	$result = userpoints_userpointsapi($params);
    	//dsm($result);
    }
  }
}

 
/**
 * Implements hook_form_alter().
 */
function mcshop_form_alter(&$form, &$form_state, $form_id) {
    if($form_id == "user_register_form" /*|| $form_id == "user_profile_form"*/)
    {
		//dsm($form_state);
        $form['#validate'][] = '_mc_user_validate';
    }
	else if(strpos($form_id, 'commerce_checkout_form_review') === 0)
	{
        $form['buttons']['continue']['#validate'][] = '_mc_checkout_validate';
	}
}

/**
 * Callback minecraft user validation
 */
function _mc_checkout_validate($form, &$form_state) {
	//dsm($form);
	//dsm($form['commerce_payment']['payment_methods']['#value']);
	$re = true;
	foreach ($form['commerce_payment']['payment_methods']['#value'] as $key => $content)
		if($key == 'commerce_userpoints|commerce_payment_commerce_userpoints')
			$re = false;
	if($re)
		return;
	
	// TODO: Connect MC Server
	$mcinfo = variable_get('mcshop_mcinfo');
	if(isset($mcinfo) && !$mcinfo->isOnline())
	{
		form_set_error('customer_profile_custom_user_p' , t('Sorry, Our MC Server is not offline. We can not guarantee the validity of your billing information.'));
		return;
	}
	$connector = new MCConnector();
	if(!$connector->connect())
		form_set_error('customer_profile_custom_user_p' , t('Sorry, Our MC Server is not offline. We can not guarantee the validity of your billing information.'));
	else
		$connector->disconnect();
}
 
/**
 * Callback minecraft user validation
 */
function _mc_user_validate($form, &$form_state) {
	if ($form['#user_category'] == 'account' || $form['#user_category'] == 'register') {
		if (isset($form_state['values']['name']) && isset($form_state['values']['field_mcpwd']['und'][0]['value'])) {

			$mcinfo = variable_get('mcshop_mcinfo');
			if(isset($mcinfo) && !$mcinfo->isOnline())
			{
				form_set_error('name', t('Sorry, Our MC Server is offline. We can not guarantee the validity of your account information.'));
				return;
			}
			$connector = new MCConnector();
			if($connector->connect()) {
				$result = $connector->checkPlayerAccount($form_state['values']['name'], $form_state['values']['field_mcpwd']['und'][0]['value']);
				$connector->disconnect();
				if(!result)
					form_set_error('name', t('Your acount information is incorrect.'));
			}
			else form_set_error('name', t('Sorry, Our MC Server is offline. We can not guarantee the validity of your account information.'));
			// TODO: test the MC User
			
		}
		else
			form_set_error('field_mcpwd[und][0][value]', t('MC password is empty.'));
	}
}
 