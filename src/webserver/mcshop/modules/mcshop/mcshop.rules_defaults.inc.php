<?php
function mcshop_default_rules_configuration_alter(&$configs) {
  // Add custom condition.
  $configs['commerce_checkout_new_account']->active = FALSE;
  $configs['commerce_checkout_order_email']->active = FALSE;
  $configs['commerce_checkout_order_status_update']->active = FALSE;
  
  $configs['commerce_payment_commerce_userpoints']
    ->condition('commerce_order_contains_product_type', array(
      'commerce_order:select' => 'commerce_order',
      'product_type' => array('recharge_product'),
      'operator' => '<',
      'value' => '1',
    ));
}
?>