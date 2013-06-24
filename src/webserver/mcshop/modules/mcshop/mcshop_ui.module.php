<?php
/**
 * @file
 * Module file for MC Shop System UI.
 */

/**
 * Implements hook_menu().
 */
function mcshop_menu() {

  
  $items['admin/reports/mcshop'] = array(
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
  );
  return $items;
}

function _mcshop_admin_instructions()
{
	$base_content = t('This is the base page of MC Admin. Enjoy!');
	return '<div>' . $base_content . '</div><br />';
}

/**
 * Implements hook_block_info().
 *
 * This hook declares what blocks are provided by the module.
 */
function mcshop_block_info() {


  // Many options are defined in hook_block_info():
  $blocks['mcshop_copyright'] = array(
    // info: The name of the block.
    'info' => t('Powered by N8LM'),
    'status' => TRUE,
    'region' => 'bottom',  // Not usually provided.
    // Block caching options (per role, per user, etc.)
    'cache' => DRUPAL_CACHE_GLOBAL,
  );

  // This sample shows how to provide default settings. In this case we'll
  // enable the block in the first sidebar and make it visible only on
  // 'node/*' pages. See the hook_block_info() documentation for these.
  $blocks['mcshop_server_info'] = array(
    'info' => t('Minecraft Server Information'),
    'status' => TRUE,
    'region' => 'sidebar_first',  // Not usually provided.
    'cache' => DRUPAL_CACHE_GLOBAL, 
  );

  return $blocks;
}

/**
 * Implements hook_block_view().
 *
 * This hook generates the contents of the blocks themselves.
 */
function mcshop_block_view($delta = '') {
  switch ($delta) {
    case 'mcshop_copyright':
      $block['content'] = t('Powered by No.8 Lightning Man');
      break;
    case 'mcshop_server_info':
      $block['subject'] = t('Server Info');
      $block['content'] = _mcshop_mcinfo_content();
      break;
  }
  return $block;
}

function _mcshop_mcinfo_content()
{
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
	return $mcinfo->display();
}

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
 * Implementation of hook_node_view().
 */
function mcshop_node_view($node, $view_mode) {
	// Remove the "Read more" links on Registrant Form teasers
	//if ($node->type == 'mc_item_display' && $view_mode == 'teaser') {
		unset($node->content['links']['node']['#links']['node-readmore']);
	//}
}
