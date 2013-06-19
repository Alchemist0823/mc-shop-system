<?php

/**
 * @file
 * Module file for MC Shop System UI.
 */


/**
 * Implements hook_commerce_checkout_page_info_alter().
 * in the Commerce module
 */
function mcshop_commerce_checkout_page_info_alter(&$checkout_pages) {
  
  //commerce_order_load()
}
 
 
/**
 * Implements hook_commerce_checkout_pane_info_atler().
 * in the Commerce module
 */
function mcshop_commerce_checkout_pane_info_atler(&$checkout_panes) {
  
  //commerce_order_load()
  //dpm($checkout_panes);
}
 
/**
 * Implements hook_user_view().
 */
function mcshop_user_view($account, $view_mode, $langcode) {
  $account->content['profile_mc'] = array(
    '#type' => 'user_profile_category',
    '#title' => 'MC Game Information',
	//'#attributes' => array('class' => array('user-mc',),),
	'#weight' => 5,
  );
  $account->content['profile_mc']['mc_name'] = array(
    '#type' => 'user_profile_item',
    '#title' => 'Your MC Account:',
	'#markup' => '<b>'.t('!username',array('!username' => format_username($account))).'</b>',
    '#prefix' => '<a id="profile-mc"></a>',
  );
}

/**
 * Implements hook_menu().
 */
function mcshop_menu() {

  
  $items['admin/mcshop'] = array(
    'type' => MENU_NORMAL_ITEM,
    'title' => 'MC Admin',
    'description' => 'Administer Minecraft Content',
    'page callback' => '_mcshop_admin_instructions',
    'weight' => -8,
    // Arguments to the page callback. Here's we'll use them just to provide
    // content for our page.
    //'page arguments' => array(t('This page is displayed by the simplest (and base) menu example. Note that the title of the page is the same as the link title. You can also <a href="!link">visit a similar page with no menu link</a>. Also, note that there is a hook_menu_alter() example that has changed the path of one of the menu items.', array('!link' => url('examples/menu_example/path_only')))),

  	'access arguments' => array('access administration pages'),
    // see the example 'examples/menu_example/permissioned/controlled' below.
    //'access callback' => TRUE,

    // 'file' => 'menu_example.module',

    // 'menu_name' => 'navigation',

    //'expanded' => TRUE
  );
  return $items;
}

function _mcshop_admin_instructions()
{
	$base_content = t('This is the base page of MC Admin. Enjoy!');
	return '<div>' . $base_content . '</div><br />';
}