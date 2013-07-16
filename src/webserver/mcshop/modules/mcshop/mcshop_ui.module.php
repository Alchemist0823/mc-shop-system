<?php
/**
 * @file
 * Module file for MC Shop System UI.
 */

/**
 * Implements hook_menu().
 */
function mcshop_ui_menu() {

  
  $items['admin/reports/mcshop'] = array(
    'type' => MENU_NORMAL_ITEM,
    'title' => t('MC Report'),
    'description' => t('Minecraft Content Reports'),
    'page callback' => '_mcshop_admin_instructions',
    'weight' => -8,
    // Arguments to the page callback. Here's we'll use them just to provide
    // content for our page.
    //'page arguments' => array(t('This page is displayed by the simplest (and base) menu example. Note that the title of the page is the same as the link title. You can also <a href="!link">visit a similar page with no menu link</a>. Also, note that there is a hook_menu_alter() example that has changed the path of one of the menu items.', array('!link' => url('examples/menu_example/path_only')))),

  	'access arguments' => array('access administration pages'),
    // see the example 'examples/menu_example/permissioned/controlled' below.
    //'access callback' => TRUE,

    'file' => 'includes/mcshop_ui.admin.inc',

    // 'menu_name' => 'navigation',
  );
  return $items;
}

/**
 * Implements hook_block_info().
 *
 * This hook declares what blocks are provided by the module.
 */
function mcshop_ui_block_info() {


  // Many options are defined in hook_block_info():
  $blocks['mcshop_copyright'] = array(
    // info: The name of the block.
    'info' => t('Powered by N8LM'),
    'status' => TRUE,
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
function mcshop_ui_block_view($delta = '') {
  switch ($delta) {
    case 'mcshop_copyright':
      $block['content'] = t('Powered by <a href="@url">No.8 Lightning Man</a>', array('@url' => 'http://mymcshop.com'));
      break;
    case 'mcshop_server_info':
      $block['subject'] = t('Server Info');
      $block['content'] = _mcshop_ui_mcinfo_content();
      break;
  }
  return $block;
}

function _mcshop_ui_mcinfo_content()
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
	return $mcinfo->display();
}

/**
 * Implements hook_user_view().
 */
function mcshop_ui_user_view($account, $view_mode, $langcode) {
  $account->content['profile_mc'] = array(
    '#type' => 'user_profile_category',
    '#title' => t('MC Game Information'),
	//'#attributes' => array('class' => array('user-mc',),),
	'#weight' => 5,
  );
  $account->content['profile_mc']['mc_name'] = array(
    '#type' => 'user_profile_item',
    '#title' => t('Your MC Account:'),
	'#markup' => '<b>'.t('!username',array('!username' => format_username($account))).'</b>',
    '#prefix' => '<a id="profile-mc"></a>',
  );
}


/**
 * Implementation of hook_node_view().
 */
function mcshop_ui_node_view($node, $view_mode) {
	// Remove the "Read more" links on Registrant Form teasers
	if ($node->type == 'minecraft_item' && $view_mode == 'teaser') {
		unset($node->content['links']['node']['#links']['node-readmore']);
	}
}
