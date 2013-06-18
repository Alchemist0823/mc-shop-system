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
 * Function Send Cmd Through MCConnecter
 */
function _mcshop_sendcmd($cmd, $args)
{
  
  require_once('MCConnector.php');
  
  $connector = new MCConnector(variable_get_value('mcshop_server_host'), variable_get_value('mcshop_server_port'));
  //$connector->connect(variable_get_value('mcshop_server_pass'));
  
  //$connector->doCommand($cmd);
  
  // TODO: 
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
    $cmd = $product->field_buycmd['und'][0]['value'];
	
	
	global $user;
	
	$args = array(
	  'player' => $user->name,
	  'quantity' => (int)$line_item->quantity,
	  'id' => 'gold',
	);
	
	_mcshop_sendcmd($cmd,$args);
	
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
	//dsm($form_state);
	
	
	require_once('MCConnector.php');
	// TODO: Connect MC Server
	$connector = new MCConnector(variable_get_value('mcshop_server_host'), variable_get_value('mcshop_server_port'));
	
	form_set_error('customer_profile_custom_user_p[field_mcplayer][und][0][value]' , t('Sorry, Our MC Server is not online. We can not guarantee the validity of your billing information'));
}
 
/**
 * Callback minecraft user validation
 */
function _mc_user_validate($form, &$form_state) {
	if ($form['#user_category'] == 'account' || $form['#user_category'] == 'register') {
		if (isset($form_state['values']['name']) && isset($form_state['values']['field_mcpwd']['und'][0]['value'])) {
			
			require_once('MCConnector.php');
			
			form_set_error('name', t('the MC Server connection is under construction.'));
			// TODO: test the MC User
			
			
		}
		else
			form_set_error('field_mcpwd[und][0][value]', t('MC password is empty.'));
	}
}
 