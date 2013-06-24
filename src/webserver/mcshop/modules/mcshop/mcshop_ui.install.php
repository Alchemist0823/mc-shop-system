<?php
/**
 * @file
 * Install, update and uninstall functions for the mcshop module.
 */


/**
 * Implements hook_uninstall().
 */
 
function mcshop_ui_uninstall() {
  variable_del('mcshop_mcinfo');
}
