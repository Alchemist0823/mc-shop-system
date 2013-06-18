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

  /*
  $items['mcshop'] = array(

    'title' => 'MC Shop',

    // Description (hover flyover for menu link). Does NOT use t(), which is
    // called automatically.
    'description' => 'Simplest possible menu type, and the parent menu entry for others',

    // Function to be called when this path is accessed.
    'page callback' => '_menu_example_basic_instructions',

    // Arguments to the page callback. Here's we'll use them just to provide
    // content for our page.
    'page arguments' => array(t('This page is displayed by the simplest (and base) menu example. Note that the title of the page is the same as the link title. You can also <a href="!link">visit a similar page with no menu link</a>. Also, note that there is a hook_menu_alter() example that has changed the path of one of the menu items.', array('!link' => url('examples/menu_example/path_only')))),

    // If the page is meant to be accessible to all users, you can set 'access
    // callback' to TRUE. This bypasses all access checks. For an explanation on
    // how to use the permissions system to restrict access for certain users,
    // see the example 'examples/menu_example/permissioned/controlled' below.
    'access callback' => TRUE,

    // If the page callback is located in another file, specify it here and
    // that file will be automatically loaded when needed.
    // 'file' => 'menu_example.module',

    // We can choose which menu gets the link. The default is 'navigation'.
    // 'menu_name' => 'navigation',

    // Show the menu link as expanded.
    'expanded' => TRUE,
  );*/
  //return items;
}