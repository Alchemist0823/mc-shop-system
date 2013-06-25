<?php
/**
 * @file
 * Contains the theme's functions to manipulate Drupal's default markup.
 *
 * A QUICK OVERVIEW OF DRUPAL THEMING
 *
 *   The default HTML for all of Drupal's markup is specified by its modules.
 *   For example, the comment.module provides the default HTML markup and CSS
 *   styling that is wrapped around each comment. Fortunately, each piece of
 *   markup can optionally be overridden by the theme.
 *
 *   Drupal deals with each chunk of content using a "theme hook". The raw
 *   content is placed in PHP variables and passed through the theme hook, which
 *   can either be a template file (which you should already be familiary with)
 *   or a theme function. For example, the "comment" theme hook is implemented
 *   with a comment.tpl.php template file, but the "breadcrumb" theme hooks is
 *   implemented with a theme_breadcrumb() theme function. Regardless if the
 *   theme hook uses a template file or theme function, the template or function
 *   does the same kind of work; it takes the PHP variables passed to it and
 *   wraps the raw content with the desired HTML markup.
 *
 *   Most theme hooks are implemented with template files. Theme hooks that use
 *   theme functions do so for performance reasons - theme_field() is faster
 *   than a field.tpl.php - or for legacy reasons - theme_breadcrumb() has "been
 *   that way forever."
 *
 *   The variables used by theme functions or template files come from a handful
 *   of sources:
 *   - the contents of other theme hooks that have already been rendered into
 *     HTML. For example, the HTML from theme_breadcrumb() is put into the
 *     $breadcrumb variable of the page.tpl.php template file.
 *   - raw data provided directly by a module (often pulled from a database)
 *   - a "render element" provided directly by a module. A render element is a
 *     nested PHP array which contains both content and meta data with hints on
 *     how the content should be rendered. If a variable in a template file is a
 *     render element, it needs to be rendered with the render() function and
 *     then printed using:
 *       <?php print render($variable); ?>
 *
 * ABOUT THE TEMPLATE.PHP FILE
 *
 *   The template.php file is one of the most useful files when creating or
 *   modifying Drupal themes. With this file you can do three things:
 *   - Modify any theme hooks variables or add your own variables, using
 *     preprocess or process functions.
 *   - Override any theme function. That is, replace a module's default theme
 *     function with one you write.
 *   - Call hook_*_alter() functions which allow you to alter various parts of
 *     Drupal's internals, including the render elements in forms. The most
 *     useful of which include hook_form_alter(), hook_form_FORM_ID_alter(),
 *     and hook_page_alter(). See api.drupal.org for more information about
 *     _alter functions.
 *
 * OVERRIDING THEME FUNCTIONS
 *
 *   If a theme hook uses a theme function, Drupal will use the default theme
 *   function unless your theme overrides it. To override a theme function, you
 *   have to first find the theme function that generates the output. (The
 *   api.drupal.org website is a good place to find which file contains which
 *   function.) Then you can copy the original function in its entirety and
 *   paste it in this template.php file, changing the prefix from theme_ to
 *   powermc_. For example:
 *
 *     original, found in modules/field/field.module: theme_field()
 *     theme override, found in template.php: powermc_field()
 *
 *   where powermc is the name of your sub-theme. For example, the
 *   zen_classic theme would define a zen_classic_field() function.
 *
 *   Note that base themes can also override theme functions. And those
 *   overrides will be used by sub-themes unless the sub-theme chooses to
 *   override again.
 *
 *   Zen core only overrides one theme function. If you wish to override it, you
 *   should first look at how Zen core implements this function:
 *     theme_breadcrumbs()      in zen/template.php
 *
 *   For more information, please visit the Theme Developer's Guide on
 *   Drupal.org: http://drupal.org/node/173880
 *
 * CREATE OR MODIFY VARIABLES FOR YOUR THEME
 *
 *   Each tpl.php template file has several variables which hold various pieces
 *   of content. You can modify those variables (or add new ones) before they
 *   are used in the template files by using preprocess functions.
 *
 *   This makes THEME_preprocess_HOOK() functions the most powerful functions
 *   available to themers.
 *
 *   It works by having one preprocess function for each template file or its
 *   derivatives (called theme hook suggestions). For example:
 *     THEME_preprocess_page    alters the variables for page.tpl.php
 *     THEME_preprocess_node    alters the variables for node.tpl.php or
 *                              for node--forum.tpl.php
 *     THEME_preprocess_comment alters the variables for comment.tpl.php
 *     THEME_preprocess_block   alters the variables for block.tpl.php
 *
 *   For more information on preprocess functions and theme hook suggestions,
 *   please visit the Theme Developer's Guide on Drupal.org:
 *   http://drupal.org/node/223440 and http://drupal.org/node/1089656
 */

/**
 * Override or insert variables into the html templates.
 *
 * @param $variables
 *   An array of variables to pass to the theme template.
 * @param $hook
 *   The name of the template being rendered ("html" in this case.)
 */
function powermc_preprocess_html(&$variables, $hook) {
  // Add additional body classes depending upon the layout.
  $layout = theme_get_setting('powermc_layout');
  switch ($layout) {
    case '1':
      if (strpos(current_path(),'forum') !== FALSE)
      {
        break;
      }
      $variables['classes_array'][] = 'sidebar-right';
      $variables['classes_array'][] = 'multi-column';
      break;
    case '2':
      $variables['classes_array'][] = 'sidebar-left';
      $variables['classes_array'][] = 'multi-column';
      break;
  }
}

/**
 * Override or insert variables in the html_tag theme function.
 *
 * Remove 'type' and 'media="all"' from stylesheet <link>s because:
 * - they are redundant in HTML5
 * - this has the added advantage of counteracting IE8/respond.js's
 *   random mis-parsing of aggregated CSS
 */
function powermc_process_html_tag(&$variables) {
  $tag = &$variables['element'];
  if ($tag['#tag'] == 'link' && $tag['#attributes']['rel'] == 'stylesheet') {
    // Remove redundant type attribute.
    unset($tag['#attributes']['type']);
    // Remove media="all" but leave others unaffected.
    if (isset($tag['#attributes']['media']) && $tag['#attributes']['media'] === 'all') {
      unset($tag['#attributes']['media']);
    }
  }
}

/**
 * Override or insert variables into the page templates.
 *
 * @param $variables
 *   An array of variables to pass to the theme template.
 * @param $hook
 *   The name of the template being rendered ("page" in this case.)
 */
function powermc_preprocess_page(&$variables, $hook) {
  // Retrieve the theme setting value for 'powermc_layout'.
  $layout = theme_get_setting('powermc_layout');
  // If either the right sidebar or left sidebar layout is selected set
  // multi_column equal to TRUE.
  if (strpos(current_path(),'forum') !== FALSE)
    $variables['multi_column'] = FALSE;
  else
    $variables['multi_column'] = ($layout == '1' || $layout == '2') ? TRUE : FALSE;

  // Retrieve the theme setting value for 'powermc_max_width' and construct the
  // max-width HTML.
  $max_width = theme_get_setting('powermc_max_width') . 'px';
  $variables['max_width'] = 'style="max-width:' . $max_width . '"';

  // Retrieve the theme setting value for 'powermc_display_main_menu'.
  $variables['display_main_menu'] = theme_get_setting('powermc_display_main_menu');
}

/**
 * Override or insert variables into the node templates.
 *
 * @param $variables
 *   An array of variables to pass to the theme template.
 * @param $hook
 *   The name of the template being rendered ("node" in this case.)
 */
function powermc_preprocess_node(&$variables, $hook) {
  // Modify Zen's default pubdate output.
  $variables['pubdate'] = '<time pubdate datetime="' . format_date($variables['node']->created, 'custom', 'c') . '">' . format_date($variables['node']->created, 'custom', 'jS F, Y') . ' &middot; ' . format_date($variables['node']->created, 'custom', 'g:ia') . '</time>';

  // Remove 'Submitted by ' and insert a middot.
  if ($variables['display_submitted']) {
    $variables['submitted'] = t('!username &middot; !datetime', array('!username' => $variables['name'], '!datetime' => $variables['pubdate']));
  }
}

/**
 * Override or insert variables into the comment templates.
 *
 * @param $variables
 *   An array of variables to pass to the theme template.
 * @param $hook
 *   The name of the template being rendered ("comment" in this case.)
 */
function powermc_preprocess_comment(&$variables, $hook) {
  // Modify Zen's default pubdate output.
  $variables['pubdate'] = '<time pubdate datetime="' . format_date($variables['comment']->created, 'custom', 'c') . '">' . format_date($variables['comment']->created, 'custom', 'jS F, Y') . ' &middot; ' . format_date($variables['comment']->created, 'custom', 'g:ia') . '</time>';

  // Replace 'replied on' with a middot.
  $variables['submitted'] = t('!username &middot; !datetime', array('!username' => $variables['author'], '!datetime' => $variables['pubdate']));
}

/**
 * Override status messages.
 *
 * Insert a 'messages-inner' div.
 */
function powermc_status_messages($variables) {
  $display = $variables['display'];
  $output = '';

  $status_heading = array(
    'status' => t('Status message'),
    'error' => t('Error message'),
    'warning' => t('Warning message'),
  );
  foreach (drupal_get_messages($display) as $type => $messages) {
    $output .= "<div class=\"messages $type\"><div class=\"messages-inner\">\n";
    if (!empty($status_heading[$type])) {
      $output .= '<h2 class="element-invisible">' . $status_heading[$type] . "</h2>\n";
    }
    if (count($messages) > 1) {
      $output .= " <ul>\n";
      foreach ($messages as $message) {
        $output .= '  <li>' . $message . "</li>\n";
      }
      $output .= " </ul>\n";
    }
    else {
      $output .= $messages[0];
    }
    $output .= "</div></div>\n";
  }
  return $output;
}
