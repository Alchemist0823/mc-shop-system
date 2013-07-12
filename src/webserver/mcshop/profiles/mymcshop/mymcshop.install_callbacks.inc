<?php

/**
 * @file
 * Contains Batch API callbacks used during installation.
 */

/**
 * BatchAPI callback.
 *
 * @see mymcshop_install_additional_modules()
 */
function _mymcshop_enable_theme($theme, &$context) {
  theme_enable(array($theme));
  variable_set('theme_default', $theme);

  $context['message'] = st('Installed the default theme.');
}

/**
 * BatchAPI callback.
 *
 * @see mymcshop_install_additional_modules()
 */
function _mymcshop_enable_module($module, $module_name, &$context) {
  module_enable(array($module), FALSE);
  $context['message'] = st('Installed %module module.', array('%module' => $module_name));
}

/**
 * BatchAPI callback.
 *
 * @see mymcshop_install_additional_modules()
 */
function _mymcshop_setup_localization($operation, &$context) {
  require_once DRUPAL_ROOT . '/includes/language.inc';
  $context['message'] = t('@operation', array('@operation' => $operation));

  // Enable en prefix for english language.
  db_update('languages')
    ->fields(array(
      'prefix' => 'en',
    ))
    ->condition('language',  'en')
    ->execute();

  // Enable language detection via url.
  $negotiation['locale-url'] = array(
    'types' => array(
      'language_content',
      'language',
      'language_url',
    ),
    'callbacks' => array(
      'language' => 'locale_language_from_url',
      'switcher' => 'locale_language_switcher_url',
      'url_rewrite' => 'locale_language_url_rewrite_url',
    ),
    'file' => 'includes/locale.inc',
    'weight' => '-8,',
    'name' => 'URL',
    'description' => t('Determine the language from the URL (Path prefix or domain).'),
    'config' => 'admin/config/regional/language/configure/url',
  );
  language_negotiation_set('language', $negotiation);
}

/**
 * BatchAPI callback.
 *
 * @see mymcshop_install_additional_modules()
 */
function _mymcshop_flush_caches($operation, &$context) {
  $context['message'] = t('@operation', array('@operation' => $operation));
  drupal_flush_all_caches();
}


/**
 * BatchAPI callback.
 *
 * @see mymcshop_import_content()
 */
/*
function _mymcshop_taxonomy_menu($operation, &$context) {
  $context['message'] = t('@operation', array('@operation' => $operation));


  // Set up taxonomy main menu.
  $vocabulary_machine_name = variable_get('mymcshop_demo_store', FALSE) ? 'collection' : 'product_category';
  if ($collection = taxonomy_vocabulary_machine_name_load($vocabulary_machine_name)) {
    $variable_name = _taxonomy_menu_build_variable('vocab_menu', $collection->vid);
    variable_set($variable_name, 'main-menu');
    $variable_name = _taxonomy_menu_build_variable('vocab_parent', $collection->vid);
    variable_set($variable_name, '0');
    $variable_name = _taxonomy_menu_build_variable('path', $collection->vid);
    variable_set($variable_name, 'mymcshop_taxonomy_term_path');
    $variable_name = _taxonomy_menu_build_variable('rebuild', $collection->vid);
    variable_set($variable_name, 1);
  }
}
*/
/**
 * BatchAPI callback.
 *
 * @see mymcshop_import_content()
 */
function _mymcshop_import($operation, $type, &$context) {
  $context['message'] = t('@operation', array('@operation' => $type));
  $migration =  Migration::getInstance($operation);
  $migration->processImport();
}

/**
 * BatchAPI callback.
 *
 * @see mymcshop_import_content()
 */
/*
function _mymcshop_example_user($operation, &$context) {
  $context['message'] = t('@operation', array('@operation' => $operation));

  // Create a sample user.
  $user = array(
    'name' => 'Sample Customer',
    'mail' => 'customer@example.com',
    'pass' => 'customer',
    'status' => 1,
  );
  $user = user_save(NULL, $user);

  // Create 3 order samples.
  _mymcshop_create_order($user->uid, 2 , TRUE);
  _mymcshop_create_order($user->uid, 5);
  _mymcshop_create_order($user->uid, 3);

}*/

/**
 * BatchAPI callback.
 *
 * @see mymcshop_import_content()
 */
function _mymcshop_post_import($operation, &$context) {
  $context['message'] = t('@operation', array('@operation' => $operation));

  _mymcshop_setup_blocks();
}



/**
 * BatchAPI callback.
 *
 * @see mymcshop_setup_userpoint()
 */
function _mymcshop_setup_userpoint($operation, &$context) {
  
  $context['message'] = t('@operation', array('@operation' => $operation));

  //autosku
  db_update('commerce_autosku_patterns')
  ->fields(array('pattern' => '[commerce-product:title]-[commerce-product:created]'))
  ->condition('product_type', 'minecraft_item')
  ->execute();

  db_update('commerce_autosku_patterns')
  ->fields(array('pattern' => 'recharge-card-[commerce-product:title]'))
  ->condition('product_type', 'recharge_product')
  ->execute();
  
  $vocabulary = taxonomy_vocabulary_machine_name_load('categories');
  if(isset($vocabulary))
  {
    $terms = array();
    $terms[] = st('Permission');
    $terms[] = st('Item');
    $terms[] = st('House');
    foreach($terms as $name) {
      $term = new stdClass();
      $term->vid = $vocabulary->vid;
      $term->name = $name;
      taxonomy_term_save($term);
    }
  }


  $vocabulary = taxonomy_vocabulary_machine_name_load('userpoints');
  if(isset($vocabulary))
  {
    $terms = array();
    $terms[] = st('MC Points');
    foreach($terms as $name) {
      $term = new stdClass();
      $term->vid = $vocabulary->vid;
      $term->name = $name;
      taxonomy_term_save($term);
    }
    $tid = null;
    $tree = taxonomy_get_tree($vocabulary->vid);
    foreach ($tree as $term) {
      $tid = $term->tid;
    }

    if(isset($tid))
    {
      variable_set('userpoints_category_default_tid', $tid);
      variable_set('userpoints_invite_tid', $tid);
      variable_set('userpoints_category_profile_display_tid', array(
      $tid => ''.$tid,
      'uncategorized' => 0,
      'all' => 0,
      ));
      variable_set('commerce_userpoints_currencies', array(
      'MCM' => array(
      'name' => 'MC Money',
      'tid' => ''.$tid,
      'code' => 'MCM',
      'symbol' => 'MC$',
      'conversion_rate' => '1',
      ),
      ));
    }
  }
}


/**
 * Configures the theme and assign blocks to the appropriate regions.
 */
function _mymcshop_setup_blocks() {
  $admin_theme = variable_get('admin_theme', 'seven');
  $default_theme = variable_get('theme_default', 'powermc');

  $blocks = array(
    array(
      'module' => 'system',
      'delta' => 'help',
      'theme' => $admin_theme,
      'status' => 1,
      'weight' => 0,
      'region' => 'help',
      'pages' => '',
      'cache' => DRUPAL_NO_CACHE,
    ),
    array(
      'module' => 'system',
      'delta' => 'help',
      'theme' => $default_theme,
      'status' => 1,
      'weight' => 0,
      'region' => 'help',
      'pages' => '',
      'cache' => DRUPAL_NO_CACHE,
    ),
    array(
      'module' => 'system',
      'delta' => 'main',
      'theme' => $default_theme,
      'status' => 1,
      'weight' => 0,
      'region' => 'content',
      'pages' => '<front>', // Do not show the block on front.
      'visibility' => 0,
      'cache' => DRUPAL_NO_CACHE,
    ),
    // commerce_cart.
    array(
      'module' => 'commerce_cart',
      'delta' => 'cart',
      'theme' => $default_theme,
      'status' => 1,
      'weight' => -1,
      'region' => 'sidebar_first',
      'pages' => "checkout*\n\rforum*",
      'cache' => DRUPAL_NO_CACHE,
    ),
    // commerce_cart.
    array(
      'module' => 'commerce_cart',
      'delta' => 'cart',
      'theme' => $default_theme,
      'status' => 1,
      'weight' => -1,
      'region' => 'sidebar_first',
      'pages' => "checkout*\n\rforum*",
    ),
    // mcshop_copyright.
    array(
      'module' => 'mcshop_ui',
      'delta' => 'mcshop_copyright',
      'theme' => $default_theme,
      'status' => 1,
      'weight' => -1,
      'region' => 'bottom',
    ),
    // mcshop_server_info.
    array(
      'module' => 'mcshop_ui',
      'delta' => 'mcshop_server_info',
      'theme' => $default_theme,
      'status' => 1,
      'weight' => 0,
      'region' => 'sidebar_first',
    ),
    // menu-footer-navigation.
    array(
      'module' => 'menu',
      'delta' => 'menu-footer-navigation',
      'theme' => $default_theme,
      'status' => 1,
      'weight' => 0,
      'title' => '<none>',
      'region' => 'footer',
    ),
    // search.
    array(
      'module' => 'search',
      'delta' => 'form',
      'theme' => $default_theme,
      'status' => 1,
      'weight' => 0,
      'region' => 'userbar',
    ),
    // navigation.
    array(
      'module' => 'system',
      'delta' => 'navigation',
      'theme' => $default_theme,
      'status' => 1,
      'weight' => 0,
      'region' => 'sidebar_first',
    ),
    // powered-by.
    array(
      'module' => 'system',
      'delta' => 'powered-by',
      'theme' => $default_theme,
      'status' => 0,
      'weight' => -8,
      'region' => -1,
    ),
    // user login.
    array(
      'module' => 'user',
      'delta' => 'login',
      'theme' => $default_theme,
      'status' => 1,
      'weight' => -2,
      'region' => 'sidebar_first',
    ),
    // user new.
    array(
      'module' => 'user',
      'delta' => 'new',
      'theme' => $default_theme,
      'status' => 1,
      'weight' => 0,
      'region' => 'sidebar_second',
    ),
    // user online.
    array(
      'module' => 'user',
      'delta' => 'online',
      'theme' => $default_theme,
      'status' => 1,
      'weight' => 0,
      'region' => 'sidebar_second',
    ),
  );
  
  $install_localization = variable_get('mymcshop_localization', FALSE);
  if($install_localization)
    $blocks[] = 
    // locale.
    array(
      'module' => 'locale',
      'delta' => 'language',
      'theme' => $default_theme,
      'status' => 1,
      'weight' => -1,
      'region' => 'sidebar_second',
    );

  drupal_static_reset();
  _block_rehash($admin_theme);
  _block_rehash($default_theme);
  foreach ($blocks as $record) {
    $module = array_shift($record);
    $delta = array_shift($record);
    $theme = array_shift($record);
    db_update('block')
      ->fields($record)
      ->condition('module', $module)
      ->condition('delta', $delta)
      ->condition('theme', $theme)
      ->execute();
  }
}

/*
 * Helper function to create a sample order.
 *
 * @param $uid
 * @param $order_state
 * @param $items_number
 */
/*
function _mymcshop_create_order($uid, $items_number, $is_cart = FALSE) {
  // Create order as a cart.
  $order = commerce_order_new($uid, 'cart');
  commerce_order_save($order);

  $message = message_create('commerce_order_created', array('uid' => $uid));
  $wrapper = entity_metadata_wrapper('message', $message);
  $wrapper->message_commerce_order->set($order);
  $wrapper->save();

  // Add products to the cart.
  $line_items = array();
  $products = entity_load('commerce_product');
  $products_key = array_keys($products);
  $products_number = count($products_key);
  $count = $products_number < $items_number ? $products_number: $items_number;
  for($i = 0; $i < $count; $i++) {
    $product = $products[$products_key[rand(0, $products_number - 1)]];
    $line_item = commerce_product_line_item_new($product, 1, $order->order_id);
    commerce_line_item_save($line_item);
    $line_items[] = array('line_item_id' => $line_item->line_item_id);

    $message = message_create('commerce_order_cart_add', array('uid' => $uid));
    $wrapper = entity_metadata_wrapper('message', $message);
    $wrapper->message_commerce_order->set($order);
    $wrapper->message_commerce_line_item->set($line_item);
    $wrapper->save();
  }
  $order->commerce_line_items[LANGUAGE_NONE] = $line_items;

  // Stop here for cart creation.
  if ($is_cart) {
    commerce_order_save($order);
    return;
  }

  // Attach customer profile.
  _mymcshop_update_order_status($order, 'checkout_checkout');
  $billing_profile_id = commerce_addressbook_get_default_profile_id($uid, 'billing');
  $shippin_profile_id = commerce_addressbook_get_default_profile_id($uid, 'shipping');
  if (!empty($billing_profile_id)) {
    $order->commerce_customer_billing[LANGUAGE_NONE][0]['profile_id'] = $billing_profile_id;
  }
  if (!empty($shippin_profile_id)) {
    $order->commerce_customer_shipping[LANGUAGE_NONE][0]['profile_id'] = $shippin_profile_id;
  }

  //@TODO Add shipping service.
  _mymcshop_update_order_status($order, 'checkout_shipping');

  // Proceed to payment.
  _mymcshop_update_order_status($order, 'checkout_review');
  $order->data['payment_method'] = 'mymcshop_payment|commerce_payment_mymcshop_payment';
  $order->data['mymcshop_payment'] = array(
    'credit_card' => array(
      'type' => 'visa',
      'number' => '4111111111111111',
      'exp_month' => '06',
      'exp_year' => '2012',

    ),
  );
  commerce_order_save($order);
  $rule = rules_config_load('commerce_payment_mymcshop_payment');
  $payment_method = commerce_payment_method_load('mymcshop_payment');
  $method_instance_id = commerce_payment_method_instance_id('mymcshop_payment', $rule);
  $method_instance = commerce_payment_method_instance_load($method_instance_id);
  $transaction = mymcshop_payment_transaction($method_instance, $order, $order->commerce_order_total[LANGUAGE_NONE][0], '');

  $message = message_create('commerce_order_payment_entered', array('uid' => $uid));
  $wrapper = entity_metadata_wrapper('message', $message);
  $wrapper->message_commerce_order->set($order);
  $wrapper->message_commerce_payment->set($transaction);
  $wrapper->save();

  _mymcshop_update_order_status($order, 'checkout_payment');
  _mymcshop_update_order_status($order, 'checkout_complete');
  _mymcshop_update_order_status($order, 'pending');
}
*/
/**
 * Helper function to update the order status and set the message.
 *
 * @param $order
 * @param $status
 */
/*
function _mymcshop_update_order_status($order, $status) {
  // We need to harcode the previous state, as the entity will not
  // have this value on run-time.
  $statuses = commerce_order_statuses();
  $arguments = array(
    '@order_status_previous' => $statuses[$order->status]['title'],
    '@order_status' => $statuses[$status]['title'],
  );
  $order = commerce_order_status_update($order, $status);

  $message = message_create('commerce_order_state', array('uid' => $order->uid, 'arguments' => $arguments));
  $wrapper = entity_metadata_wrapper('message', $message);
  $wrapper->message_commerce_order->set($order);
  $wrapper->save();
}*/
