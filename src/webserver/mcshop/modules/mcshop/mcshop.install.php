<?php
/**
 * @file
 * Install, update and uninstall functions for the mcshop module.
 */

/**
 * Implements hook_install()
 */
 
function mcshop_install() {

	$field_bases = array();
	
	// Exported field_base: 'mc_buycmd'
	$field_bases['mc_buycmd'] = array(
			'active' => 1,
			'cardinality' => 1,
			'deleted' => 0,
			'entity_types' => array(),
			'field_name' => 'mc_buycmd',
			'foreign keys' => array(
					'format' => array(
							'columns' => array(
									'format' => 'format',
							),
							'table' => 'filter_format',
					),
			),
			'indexes' => array(
					'format' => array(
							0 => 'format',
					),
			),
			'locked' => 0,
			'module' => 'text',
			'settings' => array(
					'max_length' => 255,
			),
			'translatable' => 0,
			'type' => 'text',
	);
	
	// Exported field_base: 'mc_pwd'
	$field_bases['mc_pwd'] = array(
			'active' => 1,
			'cardinality' => 1,
			'deleted' => 0,
			'entity_types' => array(),
			'field_name' => 'mc_pwd',
			'field_permissions' => array(
					'type' => 2,
			),
			'foreign keys' => array(
					'format' => array(
							'columns' => array(
									'format' => 'format',
							),
							'table' => 'filter_format',
					),
			),
			'indexes' => array(
					'format' => array(
							0 => 'format',
					),
			),
			'locked' => 0,
			'module' => 'text',
			'settings' => array(
					'max_length' => 30,
			),
			'translatable' => 0,
			'type' => 'text',
	);
	
	$field_instances = array();
	
	$field_instances['user-user-mc_pwd'] = array(
			'bundle' => 'user',
			'default_value' => NULL,
			'deleted' => 0,
			'description' => 'Minecraft Password. It is used to test your minecraft account.',
			'display' => array(
					'default' => array(
							'label' => 'above',
							'settings' => array(),
							'type' => 'hidden',
							'weight' => 2,
					),
			),
			'entity_type' => 'user',
			'field_name' => 'mc_pwd',
			'label' => 'MC password',
			'required' => 1,
			'settings' => array(
					'text_processing' => 0,
					'user_register_form' => 1,
			),
			'widget' => array(
					'active' => 1,
					'module' => 'text',
					'settings' => array(
							'size' => 60,
					),
					'type' => 'text_textfield',
					'weight' => 31,
			),
	);
	
	// Exported field_instance: 'commerce_product-minecraft_item-mc_buycmd'
	$field_instances['commerce_product-minecraft_item-mc_buycmd'] = array(
			'bundle' => 'minecraft_item',
			'default_value' => NULL,
			'deleted' => 0,
			'description' => '<b>Minecraft Console Command</b> when you buy item.',
			'display' => array(
					'default' => array(
							'label' => 'hidden',
							'settings' => array(),
							'type' => 'hidden',
							'weight' => 1,
					),
					'line_item' => array(
							'label' => 'above',
							'settings' => array(),
							'type' => 'hidden',
							'weight' => 0,
					),
					'node_teaser' => array(
							'label' => 'above',
							'settings' => array(),
							'type' => 'hidden',
							'weight' => 0,
					),
			),
			'entity_type' => 'commerce_product',
			'field_name' => 'mc_buycmd',
			'label' => 'MC BuyCommands',
			'required' => 1,
			'settings' => array(
					'text_processing' => 0,
					'user_register_form' => FALSE,
			),
			'widget' => array(
			'active' => 1,
			'module' => 'text',
			'settings' => array(
			'size' => 60,
			),
			'type' => 'text_textfield',
			'weight' => 36,
			),
	);
	
	
	$permissions = array();

	$permissions['create mc_pwd'] = array(
			'name' => 'create mc_pwd',
			'roles' => array(
					'administrator' => 'administrator',
					'anonymous user' => 'anonymous user',
			),
			'module' => 'field_permissions',
	);
	
	// Exported permission: edit field_mcpwd.
	$permissions['edit mc_pwd'] = array(
			'name' => 'edit mc_pwd',
			'roles' => array(
					'administrator' => 'administrator',
			),
			'module' => 'field_permissions',
	);
	
	// Exported permission: edit own field_mcpwd.
	$permissions['edit own mc_pwd'] = array(
			'name' => 'edit own mc_pwd',
			'roles' => array(
					'administrator' => 'administrator',
			),
			'module' => 'field_permissions',
	);
	
	// Exported permission: view field_mcpwd.
	$permissions['view mc_pwd'] = array(
			'name' => 'view mc_pwd',
			'roles' => array(
					'administrator' => 'administrator',
			),
			'module' => 'field_permissions',
	);
	
	// Exported permission: view own field_mcpwd.
	$permissions['view own mc_pwd'] = array(
			'name' => 'view own mc_pwd',
			'roles' => array(
					'administrator' => 'administrator',
			),
			'module' => 'field_permissions',
	);
	
	
  $default_types = array(
    'minecraft_item' => array(
      'type' => 'minecraft_item',
      'name' => 'Minecraft Item',
      'description' => 'Minecraft item',
      'help' => '',
      'revision' => 1,
    ),
    'recharge_product' => array(
      'type' => 'recharge_product',
      'name' => 'Recharge Product',
      'description' => 'Recharge Product',
      'help' => '',
      'revision' => 1,
    ),
  );
	
    $existing_types = commerce_product_ui_commerce_product_type_info();
    foreach ($default_types as $type) {
      // Add or update.
      if (!isset($existing_types[$type['type']])) {
        $type['is_new'] = TRUE;
      }
      // Use UI function because it provides already the logic we need
      commerce_product_ui_product_type_save($type, TRUE, TRUE);
    }
}


/**
 * Implements of hook_features_rebuild().
 * Rebuilds fields from code defaults.
 */
/*
function field_base_features_rebuild($module) {
	if ($fields = features_get_default('field_base', $module)) {
		field_info_cache_clear();

		// Load all the existing field bases up-front so that we don't
		// have to rebuild the cache all the time.
		$existing_fields = field_info_fields();

		foreach ($fields as $field) {
			// Create or update field.
			if (isset($existing_fields[$field['field_name']])) {
				$existing_field = $existing_fields[$field['field_name']];
				if ($field + $existing_field != $existing_field) {
					field_update_field($field);
				}
			}
			else {
				field_create_field($field);
				$existing_fields[$field['field_name']] = $field;
			}
			variable_set('menu_rebuild_needed', TRUE);
		}
	}
}
*/
/**
 * Implements of hook_features_rebuild().
 * Rebuilds field instances from code defaults.
 */
/*
function field_instance_features_rebuild($module) {
	if ($instances = features_get_default('field_instance', $module)) {
		field_info_cache_clear();

		// Load all the existing instances up-front so that we don't
		// have to rebuild the cache all the time.
		$existing_instances = field_info_instances();

		foreach ($instances as $field_instance) {
			// If the field base information does not exist yet, cancel out.
			if (!field_info_field($field_instance['field_name'])) {
				continue;
			}

			// Create or update field instance.
			if (isset($existing_instances[$field_instance['entity_type']][$field_instance['bundle']][$field_instance['field_name']])) {
				$existing_instance = $existing_instances[$field_instance['entity_type']][$field_instance['bundle']][$field_instance['field_name']];
				if ($field_instance + $existing_instance != $existing_instance) {
					field_update_instance($field_instance);
				}
			}
			else {
				field_create_instance($field_instance);
				$existing_instances[$field_instance['entity_type']][$field_instance['bundle']][$field_instance['field_name']] = $field_instance;
			}
		}

		if ($instances) {
			variable_set('menu_rebuild_needed', TRUE);
		}
	}
}
*/


/**
 * Implements hook_uninstall().
 */
 
function mcshop_uninstall() {
  variable_del('mcshop_server_host');
  variable_del('mcshop_server_port');
  variable_del('mcshop_server_pass');
  
  variable_del('mcshop_mcconnector');
}
