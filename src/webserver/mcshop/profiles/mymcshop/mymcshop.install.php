<?php
/**
 * @file
 * Install, update and uninstall functions for the My MCShop System installation profile.
 */

/**
* Implements hook_install_tasks()
*/
function mymcshop_install_tasks($install_state) {

  $tasks = array();
  $current_task = variable_get('install_task', 'done');

  $tasks['mymcshop_configure_site_form'] = array(
    'display_name' => st('Configure store'),
    'type' => 'form',
  );
  $tasks['mymcshop_install_additional_modules'] = array(
    'display_name' => st('Install additional functionality'),
    'type' => 'batch',  
    'display' => strpos($current_task, 'mymcshop_') !== FALSE,
  );

  $tasks['mymcshop_import_content'] = array(
    'display_name' => st('Import content'),
    'type' => 'batch',
    // Show this task only after the Kickstart steps have bene reached.
    'display' => strpos($current_task, 'mymcshop_') !== FALSE,
  );
  
  $needs_translations = variable_get('mymcshop_localization', FALSE);

  $tasks['mymcshop_install_import_translation'] = array(
    'display_name' => st('Set up translations'),
    'display' => $needs_translations,
    'run' => $needs_translations ? INSTALL_TASK_RUN_IF_NOT_COMPLETED : INSTALL_TASK_SKIP,
    'type' => 'batch',
  );
  
  return $tasks;
}


/**
 * Implements hook_install_tasks_alter().
 */
function mymcshop_install_tasks_alter(&$tasks, $install_state) {
  $tasks['install_finished']['function'] = 'mymcshop_install_finished';
  $tasks['install_select_profile']['display'] = FALSE;

  unset($tasks['install_import_locales']);
  unset($tasks['install_import_locales_remaining']);
  //$tasks['install_select_locale']['display'] = FALSE;
  //$tasks['install_select_locale']['run'] = INSTALL_TASK_SKIP;

  // The "Welcome" screen needs to come after the first two steps
  // (profile and language selection), despite the fact that they are disabled.
  $new_task['install_welcome'] = array(
    'display' => TRUE,
    'display_name' => st('Welcome'),
    'type' => 'form',
    'run' => isset($install_state['parameters']['welcome']) ? INSTALL_TASK_SKIP : INSTALL_TASK_RUN_IF_REACHED,
  );
  
  _mymcshop_set_theme('seven');
  
  $old_tasks = $tasks;
  $tasks = array_slice($old_tasks, 0, 2) + $new_task + array_slice($old_tasks, 2);
}


/**
 * Force-set a theme at any point during the execution of the request.
 *
 * Drupal doesn't give us the option to set the theme during the installation
 * process and forces enable the maintenance theme too early in the request
 * for us to modify it in a clean way.
 */
function _mymcshop_set_theme($target_theme) {
  if ($GLOBALS['theme'] != $target_theme) {
    unset($GLOBALS['theme']);

    drupal_static_reset();
    $GLOBALS['conf']['maintenance_theme'] = $target_theme;
    _drupal_maintenance_theme();
  }
}

/**
 * Task callback: shows the welcome screen.
 */
function install_welcome($form, &$form_state, &$install_state) {
  drupal_set_title(st('Lisence Summary'));
  $message = '<p>' . st('Thank you for choosing My Minecraft Shop System, a product offered by No.8 Lightning Man Workgroup.') . '</p>';
  $eula = '<p>' . st('While we have a rather long, boring License just like any other technology company, here is a short summary of some key items we feel are important:') . '</p>';
  
  $items = array();
  
  $studio_link = l("No.8 Lightning Man", "http://www.n8lm.cn/", array('attributes' => array('target' => '_blank')));
  
  $items[] = st("My Minecraft Shop System is made by !comp.", array('!comp' => $studio_link));
  $items[] = st('The website part of My Minecraft Shop System is commercial product. Anyone use it should buy it on No.8 Lightning Man Studio.');
  $items[] = st('My Minecraft Shop System will collect all of payment data from your server to guarantee the fair of profit-share.');
  $items[] = st('If you have chosen the share profit plan but don not share profit with us, we will shut down your MyMCShop System.');
  $eula .= theme('item_list', array('items' => $items));
  $eula_link = l('License and User Agreement', 'http://mymcshop.com/agreement', array('attributes' => array('target' => '_blank')));
  $eula .= '<p>' . st('That is it for the main points. The full !policy can be viewed on our website.  Thank you again for choosing MyMCShop System!', array('!policy' => $eula_link)) . '</p>';
  $form = array();
  $form['welcome_message'] = array(
    '#markup' => $message,
  );
  $form['eula'] = array(
    '#prefix' => '<div id="eula-installation-welcome">',
    '#markup' => $eula,
  );
  $form['eula-accept'] = array(
    '#title' => st('I agree to the License and User Agreement'),
    '#type' => 'checkbox',
    '#suffix' => '</div>',
  );
  $form['actions'] = array(
    '#type' => 'actions',
  );
  $form['actions']['submit'] = array(
    '#type' => 'submit',
    '#value' => st("Let's Get Started!"),
    '#states' => array(
      'disabled' => array(
        ':input[name="eula-accept"]' => array('checked' => FALSE),
      ),
    ),
    '#weight' => 10,
  );
  return $form;
}


function install_welcome_validate($form, &$form_state) {
  $result = mymcshop_http_request('http://mymcshop.com/validate');
  if (isset($result) && isset($result->data)) {
    if($result->data == '1')
      return TRUE;
  }
  form_set_error('', st('Please purchase MC Shop System from <a href="@url">No.8 Lightning Man Studio!</a>', array('@url' => 'http://mymcshop.com/')));
  return FALSE;
}


function install_welcome_submit($form, &$form_state) {
  global $install_state;

  $install_state['parameters']['welcome'] = 'done';
  //$install_state['parameters']['locale'] = 'en';
}


/**
 * Task callback: returns the form allowing the user to add example store content on install.
 */
function mymcshop_configure_site_form() { //TODO
  include_once DRUPAL_ROOT . '/includes/iso.inc';

  drupal_set_title(st('Configure Site'));

  // Prepare all the options for sample content.
  
  // Set up Minecraft Server MCShop Plugin Information
  $form['mcshop_gameserver'] = array(
    '#type' => 'fieldset',
    '#title' => st('Minecraft Server Settings'),
  );
  $form['mcshop_gameserver']['mcshop_server_host'] = array(
    '#type' => 'textfield',
    '#title' => st('Minecraft Server Host'),
    '#default_value' => variable_get('mcshop_server_host'),
  );
  $form['mcshop_gameserver']['mcshop_server_port'] = array(
    '#type' => 'textfield',
    '#title' => st('Minecraft Server MCShop Plugin Port'),
    '#default_value' => variable_get('mcshop_server_port'),
  );
  $form['mcshop_gameserver']['mcshop_server_pass'] = array(
    '#type' => 'textfield',
    '#title' => st('Minecraft Server MCShop Plugin Password'),
    '#default_value' => variable_get('mcshop_server_pass'),
  );

  // Prepare all the options for sample content.
  $options = array(
      '1' => st('Yes'),
      '0' => st('No'),
  );

  
  $form['localization'] = array(
      '#type' => 'fieldset',
      '#title' => st('Localization'),
  );
  $form['localization']['install_localization'] = array(
      '#type' => 'radios',
      '#title' => st('Do you want to be able to translate the interface of your store?'),
      '#options' => $options,
      '#default_value' => '0',
  );

  $form['functionality'] = array(
      '#type' => 'fieldset',
      '#title' => st('Extra Functionality'),
  );
  $options_selection = array(
    'menus' => st('Custom <strong>admin menu</strong> designed for store owners.'),
    'forum' => st('Mincraft <strong>Forum</strong> functionality.'),
    'ga'    => st('<strong>Google Analytics</strong> functionality.'),
  );
  $form['functionality']['extras'] = array(
    '#type' => 'checkboxes',
    '#options' => $options_selection,
    '#title' => st('Install additional functionality'),
    '#states' => array(
      'visible' => array(
        ':input[name="install_demo_store"]' => array('value' => '0'),
      ),
    ),
  );

  // Build a currency options list from all defined currencies.
  $options = array();
  foreach (commerce_currencies(FALSE, TRUE) as $currency_code => $currency) {
    $options[$currency_code] = t('@code - !name', array(
      '@code' => $currency['code'],
      '@symbol' => $currency['symbol'],
      '!name' => $currency['name']
    ));

    if (!empty($currency['symbol'])) {
      $options[$currency_code] .= ' - ' . check_plain($currency['symbol']);
    }
  }

  $form['commerce_default_currency_wrapper'] = array(
    '#type' => 'fieldset',
    '#title' => st('Currency'),
  );
  $form['commerce_default_currency_wrapper']['commerce_default_currency'] = array(
    '#type' => 'select',
    '#title' => st('Default store currency'),
    '#options' => $options,
    '#default_value' => commerce_default_currency(),
  );

  $form['actions'] = array('#type' => 'actions');
  $form['actions']['submit'] = array(
    '#type' => 'submit',
    '#value' => st('Create and Finish'),
    '#weight' => 15,
  );
  return $form;
}

/**
 * Submit callback: creates the requested sample content.
 */
function mymcshop_configure_site_form_submit(&$form, &$form_state) {
  
  variable_set('mymcshop_localization', $form_state['values']['install_localization']);
  variable_set('mymcshop_selected_extras', $form_state['values']['extras']);
  
  variable_set('commerce_default_currency', $form_state['values']['commerce_default_currency']);
  variable_set('commerce_enabled_currencies', array($form_state['values']['commerce_default_currency'] => $form_state['values']['commerce_default_currency'], 'MCM' => 'MCM'));

  variable_set('mcshop_server_host', $form_state['values']['mcshop_server_host']);
  variable_set('mcshop_server_port', $form_state['values']['mcshop_server_port']);
  variable_set('mcshop_server_pass', $form_state['values']['mcshop_server_pass']);
}


/**
 * Task callback: uses Batch API to import modules based on user selection.
 * Installs all demo store modules if requested, or any modules providing
 * additional functionality to the base install.
 *
 * Any modules providing custom blocks should be enabled here, and not before
 * (as an install profile dependency), because the themes are setup during
 * mymcshop_install(), which means region assignment can only happen
 * after that.
 */
function mymcshop_install_additional_modules() {

    $modules = array(
      'mymcshop_commerce',
      'mymcshop_product',
      'mymcshop_migrate',
    );
    $selected_extras = variable_get('mymcshop_selected_extras', array());
    if (!empty($selected_extras['menus'])) {
      $modules[] = 'mymcshop_menu';
    }
    if (!empty($selected_extras['forum'])) {
      $modules[] = 'mymcshop_forum';
    }
    if (!empty($selected_extras['ga'])) {
      $modules[] = 'googleanalytics';
    }
    
  $install_localization = variable_get('mymcshop_localization', FALSE);
  if ($install_localization) {
    $modules[] = 'locale';
    $modules[] = 'i18n';
    $modules[] = 'l10n_update';
    $modules[] = 'l10n_client';
  }

  $store_country = variable_get('mymcshop_store_country', 'US');
  
  // Enable Commerce Alipay Checkout for China.
  if (in_array($store_country, array('ZH','TW','CN'))) {
    $modules[] = 'commerce_alipay';
  }

  // Resolve the dependencies now, so that module_enable() doesn't need
  // to do it later for each individual module (which kills performance).
  $files = system_rebuild_module_data();
  $modules_sorted = array();
  foreach ($modules as $module) {
    if ($files[$module]->requires) {
      // Create a list of dependencies that haven't been installed yet.
      $dependencies = array_keys($files[$module]->requires);
      $dependencies = array_filter($dependencies, '_mymcshop_filter_dependencies');
      // Add them to the module list.
      $modules = array_merge($modules, $dependencies);
    }
  }
  $modules = array_unique($modules);
  foreach ($modules as $module) {
    $modules_sorted[$module] = $files[$module]->sort;
  }
  arsort($modules_sorted);

  $operations = array();
  // Enable and set as default the correct theme.
  $theme = 'powermc';
  $operations[] = array('_mymcshop_enable_theme', array($theme));
  // Enable the selected modules.
  foreach ($modules_sorted as $module => $weight) {
    $operations[] = array('_mymcshop_enable_module', array($module, $files[$module]->info['name']));
  }
  if ($install_localization) {
    $operations[] = array('_mymcshop_setup_localization', array(t('Configured localization.')));
  }
  $operations[] = array('_mymcshop_flush_caches', array(t('Flushed caches.')));

  $batch = array(
    'title' => t('Installing additional functionality'),
    'operations' => $operations,
    'file' => drupal_get_path('profile', 'mymcshop') . '/mymcshop.install_callbacks.inc',
  );

  return $batch;
}

/**
 * array_filter() callback used to filter out already installed dependencies.
 */
function _mymcshop_filter_dependencies($dependency) {
  return !module_exists($dependency);
}

/**
 * Task callback: return a batch API array with the products to be imported.
 */
function mymcshop_import_content() {
  // Fixes problems when the CSV files used for importing have been created
  // on a Mac, by forcing PHP to detect the appropriate line endings.
  ini_set("auto_detect_line_endings", TRUE);

  $operations[] = array('_mymcshop_setup_userpoint', array(t('Setup userpoint.')));

  // Run all available migrations.
  $migrations = migrate_migrations();
  foreach ($migrations as $machine_name => $migration) {
    $operations[] = array('_mymcshop_import', array($machine_name, t('Importing content.')));
  }
  // Perform post-import tasks.
  $operations[] = array('_mymcshop_post_import', array(t('Completing setup.')));
  
  $batch = array(
    'title' => t('Importing content'),
    'operations' => $operations,
    'file' => drupal_get_path('profile', 'mymcshop') . '/mymcshop.install_callbacks.inc',
  );

  return $batch;
}


/**
 * Task callback:
 *
 * @param $install_state
 *   An array of information about the current installation state.
 */
function mymcshop_install_import_translation(&$install_state) {
  // Enable installation language as default site language.
  include_once DRUPAL_ROOT . '/includes/locale.inc';
  $install_locale = $install_state['parameters']['locale'];
  locale_add_language($install_locale, NULL, NULL, NULL, '', NULL, 1, TRUE);

  // Build batch with l10n_update module.
  $history = l10n_update_get_history();
  module_load_include('check.inc', 'l10n_update');
  $available = l10n_update_available_releases();
  $updates = l10n_update_build_updates($history, $available);

  module_load_include('batch.inc', 'l10n_update');
  $updates = _l10n_update_prepare_updates($updates, NULL, array());
  $batch = l10n_update_batch_multiple($updates, LOCALE_IMPORT_KEEP);
  return $batch;
}


/**
 * Custom installation task; perform final steps and redirect the user to the new site if there are no errors.
 *
 * @param $install_state
 *   An array of information about the current installation state.
 *
 * @return
 *   A message informing the user about errors if there was some.
 */
function mymcshop_install_finished(&$install_state) {
  drupal_set_title(st('@drupal installation complete', array('@drupal' => drupal_install_profile_distribution_name())), PASS_THROUGH);
  $messages = drupal_set_message();

  // Remember the profile which was used.
  variable_set('install_profile', drupal_get_profile());
  variable_set('install_task', 'done');

  // Flush all caches to ensure that any full bootstraps during the installer
  // do not leave stale cached data, and that any content types or other items
  // registered by the install profile are registered correctly.
  drupal_flush_all_caches();

  // Install profiles are always loaded last
  db_update('system')
    ->fields(array('weight' => 1000))
    ->condition('type', 'module')
    ->condition('name', drupal_get_profile())
    ->execute();

  // Cache a fully-built schema.
  drupal_get_schema(NULL, TRUE);

  // Run cron to populate update status tables (if available) so that users
  // will be warned if they've installed an out of date Drupal version.
  // Will also trigger indexing of profile-supplied content or feeds.
  drupal_cron_run();

  if (isset($messages['error'])) {
    $output = '<p>' . (isset($messages['error']) ? st('Review the messages above before visiting <a href="@url">your new site</a>.', array('@url' => url(''))) : st('<a href="@url">Visit your new site</a>.', array('@url' => url('')))) . '</p>';
    return $output;
  }
  else {
    // Since any module can add a drupal_set_message, this can bug the user
    // when we redirect him to the front page. For a better user experience,
    // remove all the message that are only "notifications" message.
    drupal_get_messages('status', TRUE);
    drupal_get_messages('completed', TRUE);
    // Migrate adds its messages under the wrong type, see #1659150.
    drupal_get_messages('ok', TRUE);

    // If we don't install drupal using Drush, redirect the user to the front
    // page.
    if (!drupal_is_cli()) {
      if (module_exists('overlay')) {
        // Special case when no clean urls.
        $fragment = empty($GLOBALS['conf']['clean_url']) ? urlencode('?q=admin/') : 'admin/';
        drupal_goto('', array('fragment' => 'overlay=' . $fragment));
      }
      else {
        drupal_goto('admin/');
      }
    }
  }
}


/**
 * Implements hook_install().
 */
function mymcshop_install() {
  // Add text formats.
  $filtered_html_format = array(
    'format' => 'filtered_html',
    'name' => 'Filtered HTML',
    'weight' => 0,
    'filters' => array(
      // URL filter.
      'filter_url' => array(
        'weight' => 0,
        'status' => 1,
      ),
      // HTML filter.
      'filter_html' => array(
        'weight' => 1,
        'status' => 1,
      ),
      // Line break filter.
      'filter_autop' => array(
        'weight' => 2,
        'status' => 1,
      ),
      // HTML corrector filter.
      'filter_htmlcorrector' => array(
        'weight' => 10,
        'status' => 1,
      ),
    ),
  );
  $filtered_html_format = (object) $filtered_html_format;
  filter_format_save($filtered_html_format);

  $full_html_format = array(
    'format' => 'full_html',
    'name' => 'Full HTML',
    'weight' => 1,
    'filters' => array(
      // URL filter.
      'filter_url' => array(
        'weight' => 0,
        'status' => 1,
      ),
      // Line break filter.
      'filter_autop' => array(
        'weight' => 1,
        'status' => 1,
      ),
      // HTML corrector filter.
      'filter_htmlcorrector' => array(
        'weight' => 10,
        'status' => 1,
      ),
    ),
  );
  $full_html_format = (object) $full_html_format;
  filter_format_save($full_html_format);

  // Enable the admin theme.
  $admin_theme = 'seven';
  theme_enable(array($admin_theme));
  variable_set('admin_theme', $admin_theme);
  variable_set('node_admin_theme', '1');

  // Insert default pre-defined node types into the database.
  $types = array(
    array(
      'type' => 'page',
      'name' => st('Basic page'),
      'base' => 'node_content',
      'description' => st("Use <em>basic pages</em> for your static content, such as an 'About us' page."),
      'custom' => 1,
      'modified' => 1,
      'locked' => 0,
    ),
  );

  foreach ($types as $type) {
    $type = node_type_set_defaults($type);
    node_type_save($type);
    node_add_body_field($type);
  }

  // "Basic page" configuration.
  variable_set('node_options_page', array('status'));
  variable_set('comment_page', COMMENT_NODE_HIDDEN);
  variable_set('node_submitted_page', FALSE);
  variable_set('pathauto_node_page_pattern', '[node:title]');

  // Enable user picture support and set the default to a square thumbnail option.
  variable_set('user_pictures', '1');
  variable_set('user_picture_dimensions', '512x512');
  variable_set('user_picture_file_size', '400');
  variable_set('user_picture_style', 'thumbnail');
  variable_set('user_picture_default', 'sites/default/files/default_images/default_user_picture.png'); //TODO
  
  // Allow visitor account creation with administrative approval.
  variable_set('user_register', USER_REGISTER_VISITORS);
  
  // Enable default permissions for system roles.
  $filtered_html_permission = filter_permission_name($filtered_html_format);
  user_role_grant_permissions(DRUPAL_ANONYMOUS_RID, array('access content', 'access comments', 'view any commerce_product entity', $filtered_html_permission));
  user_role_grant_permissions(DRUPAL_AUTHENTICATED_RID, array('access content', 'access comments', 'access checkout', 'view own commerce_order entities', 'view any commerce_product entity', 'post comments', 'skip comment approval', $filtered_html_permission));

  // Create a default role for site administrators, with all available permissions assigned.
  $admin_role = new stdClass();
  $admin_role->name = 'administrator';
  $admin_role->weight = 2;
  user_role_save($admin_role);
  user_role_grant_permissions($admin_role->rid, array_keys(module_invoke_all('permission')));
  // Set this as the administrator role.
  variable_set('user_admin_role', $admin_role->rid);

  // Assign user 1 the "administrator" role.
  db_insert('users_roles')
    ->fields(array('uid' => 1, 'rid' => $admin_role->rid))
    ->execute();

  // Create a Home link in the main menu.
  $item = array(
    'link_title' => st('Home'),
    'link_path' => '<front>',
    'menu_name' => 'main-menu',
  );
  menu_link_save($item);
  // Update the menu router information.
  menu_rebuild();
  /*
  // Set Mimemail.
  variable_set('mimemail_format', 'full_html');
  // Set checkout progress.
  variable_set('commerce_checkout_progress_link', 0);
  variable_set('commerce_checkout_progress_list_type', 'ol');
  variable_set('commerce_checkout_progress_block_pages', array_keys(commerce_checkout_pages()));
  // Configure Chosen.
  variable_set('chosen_jquery_selector', '.view-filters .views-exposed-form select');
  variable_set('chosen_minimum', 0);
  variable_set('chosen_minimum_width', 200);
  variable_set('chosen_search_contains', TRUE);*/

  // Create the default Search API server.
  /*
  $values = array(
    'machine_name' => 'frontend',
    'name' => 'Frontend',
    'description' => '',
    'class' => 'search_api_db_service',
    'options' => array(
      'database' => 'default:default',
      'min_chars' => 3,
    ),
  );
  search_api_server_insert($values);
  */
  
  // Enable automatic title replacement for node and commerce product bundles.
  /*
  foreach (array('node', 'commerce_product') as $entity_type) {
    $title_settings = array(
      'auto_attach' => array(
        'title' => 'title',
      ),
      'hide_label' => array(
        'entity' => 'entity',
        'page' => 0,
      ),
    );
    variable_set('title_' . $entity_type, $title_settings);
  }*/
  
  // Enable the admin theme.
  db_update('system')
    ->fields(array('status' => 1))
    ->condition('type', 'theme')
    ->condition('name', 'seven')
    ->execute();
  variable_set('admin_theme', 'seven');
  variable_set('node_admin_theme', '1');
}

/**
* Implements hook_install_tasks() callback
*/
function mymcshop_configure_site_features() {
  // Create user roles
  /*
  $role = new stdClass();
  $role->name = 'editor';
  user_role_save($role);
  // Revert features
  $features = features_get_features();
    foreach($features as $name => $feature) {
    if($feature->status) {
      features_revert(array($name => array('variable', 'user_permission')));
    }
  } */
  cache_clear_all(); 
  // Enable custom theme
  theme_enable(array('powermc'));
  variable_set('theme_default', 'powermc');
}


/**
 * Perform an HTTP request.
 *
 * We cannot use drupal_http_request() at install, see http://drupal.org/node/527484
 *
 * This is a flexible and powerful HTTP client implementation. Correctly
 * handles GET, POST, PUT or any other HTTP requests. Handles redirects.
 *
 * @param $url
 *   A string containing a fully qualified URI.
 * @param array $options
 *   (optional) An array that can have one or more of the following elements:
 *   - headers: An array containing request headers to send as name/value pairs.
 *   - method: A string containing the request method. Defaults to 'GET'.
 *   - data: A string containing the request body, formatted as
 *     'param=value&param=value&...'. Defaults to NULL.
 *   - max_redirects: An integer representing how many times a redirect
 *     may be followed. Defaults to 3.
 *   - timeout: A float representing the maximum number of seconds the function
 *     call may take. The default is 30 seconds. If a timeout occurs, the error
 *     code is set to the HTTP_REQUEST_TIMEOUT constant.
 *   - context: A context resource created with stream_context_create().
 *
 * @return object
 *   An object that can have one or more of the following components:
 *   - request: A string containing the request body that was sent.
 *   - code: An integer containing the response status code, or the error code
 *     if an error occurred.
 *   - protocol: The response protocol (e.g. HTTP/1.1 or HTTP/1.0).
 *   - status_message: The status message from the response, if a response was
 *     received.
 *   - redirect_code: If redirected, an integer containing the initial response
 *     status code.
 *   - redirect_url: If redirected, a string containing the redirection location.
 *   - error: If an error occurred, the error message. Otherwise not set.
 *   - headers: An array containing the response headers as name/value pairs.
 *     HTTP header names are case-insensitive (RFC 2616, section 4.2), so for
 *     easy access the array keys are returned in lower case.
 *   - data: A string containing the response body that was received.
 */
function mymcshop_http_request($url, array $options = array()) {
  $result = new stdClass();

  // Parse the URL and make sure we can handle the schema.
  $uri = @parse_url($url);

  if ($uri == FALSE) {
    $result->error = 'unable to parse URL';
    $result->code = -1001;
    return $result;
  }

  if (!isset($uri['scheme'])) {
    $result->error = 'missing schema';
    $result->code = -1002;
    return $result;
  }

  timer_start(__FUNCTION__);

  // Merge the default options.
  $options += array(
      'headers' => array(),
      'method' => 'GET',
      'data' => NULL,
      'max_redirects' => 3,
      'timeout' => 30.0,
      'context' => NULL,
  );
  // stream_socket_client() requires timeout to be a float.
  $options['timeout'] = (float) $options['timeout'];

  switch ($uri['scheme']) {
    case 'http':
    case 'feed':
      $port = isset($uri['port']) ? $uri['port'] : 80;
      $socket = 'tcp://' . $uri['host'] . ':' . $port;
      // RFC 2616: "non-standard ports MUST, default ports MAY be included".
      // We don't add the standard port to prevent from breaking rewrite rules
      // checking the host that do not take into account the port number.
      $options['headers']['Host'] = $uri['host'] . ($port != 80 ? ':' . $port : '');
      break;
    case 'https':
      // Note: Only works when PHP is compiled with OpenSSL support.
      $port = isset($uri['port']) ? $uri['port'] : 443;
      $socket = 'ssl://' . $uri['host'] . ':' . $port;
      $options['headers']['Host'] = $uri['host'] . ($port != 443 ? ':' . $port : '');
      break;
    default:
      $result->error = 'invalid schema ' . $uri['scheme'];
      $result->code = -1003;
      return $result;
  }

  if (empty($options['context'])) {
    $fp = @stream_socket_client($socket, $errno, $errstr, $options['timeout']);
  }
  else {
    // Create a stream with context. Allows verification of a SSL certificate.
    $fp = @stream_socket_client($socket, $errno, $errstr, $options['timeout'], STREAM_CLIENT_CONNECT, $options['context']);
  }

  // Make sure the socket opened properly.
  if (!$fp) {
    // When a network error occurs, we use a negative number so it does not
    // clash with the HTTP status codes.
    $result->code = -$errno;
    $result->error = trim($errstr) ? trim($errstr) : t('Error opening socket @socket', array('@socket' => $socket));

    // Mark that this request failed. This will trigger a check of the web
    // server's ability to make outgoing HTTP requests the next time that
    // requirements checking is performed.
    // See system_requirements()
    // variable_set('drupal_http_request_fails', TRUE);

    return $result;
  }

  // Construct the path to act on.
  $path = isset($uri['path']) ? $uri['path'] : '/';
  if (isset($uri['query'])) {
    $path .= '?' . $uri['query'];
  }

  // Merge the default headers.
  $options['headers'] += array(
      'User-Agent' => 'MyMCShop (+http://mymcshop.com)',
  );

  // Only add Content-Length if we actually have any content or if it is a POST
  // or PUT request. Some non-standard servers get confused by Content-Length in
  // at least HEAD/GET requests, and Squid always requires Content-Length in
  // POST/PUT requests.
  $content_length = strlen($options['data']);
  if ($content_length > 0 || $options['method'] == 'POST' || $options['method'] == 'PUT') {
    $options['headers']['Content-Length'] = $content_length;
  }

  // If the server URL has a user then attempt to use basic authentication.
  if (isset($uri['user'])) {
    $options['headers']['Authorization'] = 'Basic ' . base64_encode($uri['user'] . (!empty($uri['pass']) ? ":" . $uri['pass'] : ''));
  }

  // If the database prefix is being used by SimpleTest to run the tests in a copied
  // database then set the user-agent header to the database prefix so that any
  // calls to other Drupal pages will run the SimpleTest prefixed database. The
  // user-agent is used to ensure that multiple testing sessions running at the
  // same time won't interfere with each other as they would if the database
  // prefix were stored statically in a file or database variable.
  $test_info = &$GLOBALS['drupal_test_info'];
  if (!empty($test_info['test_run_id'])) {
    $options['headers']['User-Agent'] = drupal_generate_test_ua($test_info['test_run_id']);
  }

  $request = $options['method'] . ' ' . $path . " HTTP/1.0\r\n";
  foreach ($options['headers'] as $name => $value) {
    $request .= $name . ': ' . trim($value) . "\r\n";
  }
  $request .= "\r\n" . $options['data'];
  $result->request = $request;
  // Calculate how much time is left of the original timeout value.
  $timeout = $options['timeout'] - timer_read(__FUNCTION__) / 1000;
  if ($timeout > 0) {
    stream_set_timeout($fp, floor($timeout), floor(1000000 * fmod($timeout, 1)));
    fwrite($fp, $request);
  }

  // Fetch response. Due to PHP bugs like http://bugs.php.net/bug.php?id=43782
  // and http://bugs.php.net/bug.php?id=46049 we can't rely on feof(), but
  // instead must invoke stream_get_meta_data() each iteration.
  $info = stream_get_meta_data($fp);
  $alive = !$info['eof'] && !$info['timed_out'];
  $response = '';

  while ($alive) {
    // Calculate how much time is left of the original timeout value.
    $timeout = $options['timeout'] - timer_read(__FUNCTION__) / 1000;
    if ($timeout <= 0) {
      $info['timed_out'] = TRUE;
      break;
    }
    stream_set_timeout($fp, floor($timeout), floor(1000000 * fmod($timeout, 1)));
    $chunk = fread($fp, 1024);
    $response .= $chunk;
    $info = stream_get_meta_data($fp);
    $alive = !$info['eof'] && !$info['timed_out'] && $chunk;
  }
  fclose($fp);

  if ($info['timed_out']) {
    $result->code = HTTP_REQUEST_TIMEOUT;
    $result->error = 'request timed out';
    return $result;
  }
  // Parse response headers from the response body.
  list($response, $result->data) = explode("\r\n\r\n", $response, 2);
  $response = preg_split("/\r\n|\n|\r/", $response);

  // Parse the response status line.
  list($protocol, $code, $status_message) = explode(' ', trim(array_shift($response)), 3);
  $result->protocol = $protocol;
  $result->status_message = $status_message;

  $result->headers = array();

  // Parse the response headers.
  while ($line = trim(array_shift($response))) {
    list($name, $value) = explode(':', $line, 2);
    $name = strtolower($name);
    if (isset($result->headers[$name]) && $name == 'set-cookie') {
      // RFC 2109: the Set-Cookie response header comprises the token Set-
      // Cookie:, followed by a comma-separated list of one or more cookies.
      $result->headers[$name] .= ',' . trim($value);
    }
    else {
      $result->headers[$name] = trim($value);
    }
  }

  $responses = array(
      100 => 'Continue',
      101 => 'Switching Protocols',
      200 => 'OK',
      201 => 'Created',
      202 => 'Accepted',
      203 => 'Non-Authoritative Information',
      204 => 'No Content',
      205 => 'Reset Content',
      206 => 'Partial Content',
      300 => 'Multiple Choices',
      301 => 'Moved Permanently',
      302 => 'Found',
      303 => 'See Other',
      304 => 'Not Modified',
      305 => 'Use Proxy',
      307 => 'Temporary Redirect',
      400 => 'Bad Request',
      401 => 'Unauthorized',
      402 => 'Payment Required',
      403 => 'Forbidden',
      404 => 'Not Found',
      405 => 'Method Not Allowed',
      406 => 'Not Acceptable',
      407 => 'Proxy Authentication Required',
      408 => 'Request Time-out',
      409 => 'Conflict',
      410 => 'Gone',
      411 => 'Length Required',
      412 => 'Precondition Failed',
      413 => 'Request Entity Too Large',
      414 => 'Request-URI Too Large',
      415 => 'Unsupported Media Type',
      416 => 'Requested range not satisfiable',
      417 => 'Expectation Failed',
      500 => 'Internal Server Error',
      501 => 'Not Implemented',
      502 => 'Bad Gateway',
      503 => 'Service Unavailable',
      504 => 'Gateway Time-out',
      505 => 'HTTP Version not supported',
  );
  // RFC 2616 states that all unknown HTTP codes must be treated the same as the
  // base code in their class.
  if (!isset($responses[$code])) {
    $code = floor($code / 100) * 100;
  }
  $result->code = $code;

  switch ($code) {
    case 200: // OK
    case 304: // Not modified
      break;
    case 301: // Moved permanently
    case 302: // Moved temporarily
    case 307: // Moved temporarily
      $location = $result->headers['location'];
      $options['timeout'] -= timer_read(__FUNCTION__) / 1000;
      if ($options['timeout'] <= 0) {
        $result->code = HTTP_REQUEST_TIMEOUT;
        $result->error = 'request timed out';
      }
      elseif ($options['max_redirects']) {
        // Redirect to the new location.
        $options['max_redirects']--;
        $result = mymcshop_http_request($location, $options);
        $result->redirect_code = $code;
      }
      $result->redirect_url = $location;
      break;
    default:
      $result->error = $status_message;
  }

  return $result;
}