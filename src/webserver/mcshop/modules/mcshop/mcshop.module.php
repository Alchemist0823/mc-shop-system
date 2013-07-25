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
 * Implements hook_commerce_checkout_complete().
 * in the Commerce module
 */
function mcshop_commerce_checkout_complete($order) {
  $order_wrapper = entity_metadata_wrapper('commerce_order', $order);
  $success = true;
  $connector = NULL;
  $log = '';
  $user = user_load($order_wrapper->uid->value());
  
  foreach ($order_wrapper->commerce_line_items as $delta => $line_item_wrapper) {
  	$product_wrapper = $line_item_wrapper->commerce_product;
    
    if($product_wrapper->type->value() == 'minecraft_item')
    {
   	  $cmd = $product_wrapper->field_buycmd->value();
   	  
   	  $args = array(
		'player' => $user->name,
		'quantity' => (int)$line_item_wrapper->quantity->value(),
   	  );
   	  
   	  if(!isset($connector))
   	  {
   	  	$connector = new MCConnector();
   	  	$success = $success && $connector->connect();
   	  }
	  if($success)
	  {
	  	$success = $connector->doCommand($cmd,$args) && $success;
	  	$log .= 'line item '.$delta.' send error';
	  }
    }
	else if($product_wrapper->type->value() == 'recharge_product')
    {

    	//global $user;
    	// Recharge Money
    	//
    	$points = (int)($line_item_wrapper->quantity->value() * $product_wrapper->field_recharge->value());
    	$params = array(
    			'points' => $points,
    			'uid' => $user->uid,
    			'operation' => $user->name.' Recharge '.$points,
    	);
    	$result = userpoints_userpointsapi($params);
    	//dsm($result);
    }
  }
  //dsm($order_wrapper);
  if(isset($connector))
    $connector->disconnect();
  if ($success)
  {
  	$log = 'send successfully';
    commerce_order_status_update($order,'completed', false, true, $log);
  }
  else 
  {
    commerce_order_status_update($order,'pending', false, true, $log);
  }
	  	// TODO: change order status Line item status
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
 * Callback minecraft checkout validation
 */
function _mc_checkout_validate($form, &$form_state) {
	//dsm($form);
	//dsm($form['commerce_payment']['payment_methods']['#value']);
	$re = true;
	foreach ($form['commerce_payment']['payment_methods']['#value'] as $key => $content)
		if($key == 'commerce_userpoints|commerce_payment_commerce_userpoints')
			$re = false;
	if($re)
		return TRUE;
	
	// TODO: Connect MC Server
	$mcinfo = variable_get('mcshop_mcinfo');
	if(isset($mcinfo) && !$mcinfo->isOnline())
	{
		form_set_error('' , t('Sorry, Our MC Server is offline. We can not guarantee the validity of your billing information.'));
		return FALSE;
	}
	$connector = new MCConnector();
	if(!$connector->connect())
	{
		form_set_error('' , t('Sorry, Our MC Server is offline. We can not guarantee the validity of your billing information.'));
		return FALSE;
	}
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
				return FALSE;
			}
			$connector = new MCConnector();
			if($connector->connect()) {
			    $result = false;
				$result = $connector->checkPlayerAccount($form_state['values']['name'], $form_state['values']['field_mcpwd']['und'][0]['value']);
				$connector->disconnect();
				if($result == 0)
				{
					form_set_error('name', t('Your acount information is incorrect.'));
					return FALSE;
				}
				else if($result == 2)
				{
					form_set_error('name', t('You need register your mcshop password.'));
					return FALSE;
				}
			}
			else
			{
			  form_set_error('name', t('Sorry, Our MC Server is offline. We can not guarantee the validity of your account information.'));
			  return FALSE;
			}
			
		}
		else
		{
			form_set_error('field_mcpwd', t('MC password is empty.'));
			return FALSE;
		}
	}
}


/**
 * Implementation of hook_commerce_cart_product_add().
 */

function mcshop_commerce_cart_product_add($order, $product, $quantity, $line_item) {

	$product_wrapper = entity_metadata_wrapper('commerce_product', $product);
	if ($product_wrapper->type->value() == 'minecraft_item' && $product_wrapper->field_buyonce->value() == 1) {
		if ($line_item) {
			$line_item->quantity = 1;
			commerce_line_item_save($line_item);
		}
	}
	// No example.
}


/**
 * Implementation of hook_form_FORMID_alter().
 */
function mcshop_form_commerce_cart_add_to_cart_form_alter(&$form, &$form_state) {
	$cart_product_ids = _mcshop_get_products_in_cart();
	$purchased_product_ids = _mcshop_get_users_purchased_products();

	$line_item = $form_state['line_item'];
	$line_item_wrapper = entity_metadata_wrapper('commerce_line_item', $line_item);
  	$product_wrapper = $line_item_wrapper->commerce_product;

	// If this was a contrib module, we'd want to check if the associated registration
	// entity is set to "Allow multiple registrations" for a user instead of just
	// checking whether the product type is "program" as is the case for this specific site
	if ($product_wrapper->type->value() == 'minecraft_item' && $product_wrapper->field_buyonce->value() == 1) { // TODO Change Condition
		// Change the "Add to Cart" button text
		//$form['submit']['#value'] = t('Get');

		if (in_array($product_wrapper->product_id->value(), $cart_product_ids)) {
			// Product is already in cart! We only want to allow a quantity of 1,
			// so disable the submit button and change its text accordingly
			$form['submit']['#disabled'] = TRUE;
			$form['submit']['#value'] = t('Already in cart');
		}
		
		if (in_array($product_wrapper->product_id->value(), $purchased_product_ids)) {
			// Product has already been purchased!
			// We only want users to register for a program once
			$form['submit']['#disabled'] = TRUE;
			$form['submit']['#value'] = t('Already purchased');
		}
		
		// TODO Registration
	}
}

/**
 * Return the product_id values for all products in the cart
 *
 * @return
 *  An array of product ids
 */
function _mcshop_get_products_in_cart() {
	$cart_product_ids = &drupal_static(__FUNCTION__);

	if (!isset($cart_product_ids)) {
		global $user;
		$cart_product_ids = array();
		$order = commerce_cart_order_load($user->uid);
		if ($order) {
			$order_wrapper = entity_metadata_wrapper('commerce_order', $order);
			foreach ($order_wrapper->commerce_line_items as $delta => $line_item_wrapper) {
				$product_wrapper = $line_item_wrapper->commerce_product;
				$cart_product_ids[] = $product_wrapper->product_id->value();
			}
		}

		$cart_product_ids = array_unique($cart_product_ids);
	}

	return $cart_product_ids;
}

/**
 * Return the product_id values for all products already purchased
 *
 * @return
 *  An array of product ids
 */

function _mcshop_get_users_purchased_products() {
	$purchased_product_ids = &drupal_static(__FUNCTION__);

	if (!isset($purchased_product_ids)) {
		global $user;
		$query = db_select('commerce_order', 'corder');
		$query->join('commerce_line_item', 'li', 'corder.order_id = li.order_id');
		$query->join('field_data_commerce_product', 'prod', 'li.line_item_id = prod.entity_id');
		$query->condition('corder.uid', $user->uid, '=')
		->condition('corder.status', 'completed', '=')
		->fields('prod', array('commerce_product_product_id'));
		$result = $query->execute();

		$purchased_product_ids = array_unique($result->fetchCol());
	}

	return $purchased_product_ids;
}

/**
 * Implementation of hook_checkout_pane_info_alter().
 */
function mcshop_commerce_checkout_pane_info_alter(&$checkout_panes)
{
  $checkout_panes['customer_profile_billing']['page'] = 'disabled';
}





/** 
 * Alipay
 * Implementation of hook_commerce_alipay_parameter_alter().
 */
function mcshop_commerce_alipay_parameter_alter(&$data, $settings, $order){

  switch ($data['service']) {


    // Payment service: DualFun (Instant Payment and Secured Transactions).
    case 'trade_create_by_buyer':
      // Payment service: Escrow Pay (Secured Transactions Interface).
    case 'create_partner_trade_by_buyer':
      global $user;
      // Wrap the order object with the Entity Metadata Wrapper.
      $order_wrapper = entity_metadata_wrapper('commerce_order', $order);
      // Get the total quantity of its product line items.
      //$quantity = commerce_line_items_quantity($order_wrapper->commerce_line_items, commerce_product_line_item_types());
      // Override transaction amount if debug mode is enabled.
      $amount = $settings['debug'] ? 1 : $order_wrapper->commerce_order_total->amount->value();
      // convert to unit price
      //$amount = $amount/$quantity;

      // Override existing properties or provide support for new ones.
      //$data['subject'] = 'Angel Subscription *' . $quantity . ' #' . $order->order_number;
      //$data['body'] = 'Angel Subscription *' . $quantity . ' #' . $order->order_number;
      $data['price'] = commerce_currency_amount_to_decimal($amount, 'CNY');
      $data['quantity'] = '1';
      $data['logistics_fee'] = '0.00';
      $data['logistics_type'] = 'EXPRESS';
      $data['logistics_payment'] = 'SELLER_PAY';
      $data['receive_name'] = $user->name;
      $data['receive_address'] = 'N/A';
      $data['receive_zip'] = '0000';
      $data['receive_phone'] = 'N/A';
      $data['receive_mobile'] = 'N/A';

      unset($data['total_fee']);

      break;

    default: break;
  }
}

