/**
 * @file
 * A JavaScript file for the theme.
 *
 * In order for this JavaScript to be loaded on pages, see the instructions in
 * the README.txt next to this file.
 */

// JavaScript should be made compatible with libraries other than jQuery by
// wrapping it with an "anonymous closure". See:
// - http://drupal.org/node/1446420
// - http://www.adequatelygood.com/2010/3/JavaScript-Module-Pattern-In-Depth
(function ($, Drupal, window, document, undefined) {

// Draw triangles across the top of the page.
// Triangles are drawn to the <canvas> element (see: html.tpl.php).
Drupal.behaviors.tri = {
  attach: function (context, settings) {
    $('#tri-canvas:not(.tri-processed)', context).addClass('tri-processed').each(function() {

      // Retrieve window width.
      var ww = $(window).width();

      // draw().
      function draw() {

        // Retrieve canvas.
        var canvas = document.getElementById('tri-canvas');

        // If rendering contexts are available...
        if (canvas.getContext){

          // Get a 2D canvas rendering context.
          var ctx = canvas.getContext('2d');  

          // Set width equal to window width.
          ctx.canvas.width = ww;

          // Define a color offset value.
          // This will be used when defining the triangle's style.
          var color = 0;

          // Start drawing triangles from the top left hand corner of
          // the page and keep moving across until we run out of space.
          for (var leftCorner = 0; leftCorner < ww; leftCorner += 50) {

            // Generate a random(ish) width value for the triangle.
            // Note: the extra 20 is added to avoid anything too narrow.
            var width = Math.floor(Math.random() * 80) + 20;

            // Ensure the width value is even.
            width = (width%2) ? width-1 : width;

            // Generate a random(ish) height value for the triangle.
            // Note: the extra 20 is added to avoid anything too short.
            var height = Math.floor(Math.random() * 40) + 20;

            // Define the triangle's style (color and opacity).
            var style = 'rgba('+(50+color)+', '+(255-color)+', 255, 0.3)';

            // Increment the color offset value.
            color += 5;

            // Draw the triangle and fill it.
            ctx.fillStyle = style;
            ctx.beginPath();
            ctx.moveTo(leftCorner, 0);
            ctx.lineTo((leftCorner+(width/2)), height);
            ctx.lineTo(leftCorner+width, 0);
            ctx.lineTo(leftCorner, 0);
            ctx.fill();
            ctx.closePath();
          }
        }
      }

      // Draw the triangles.
      draw();

      // Redraw the triangles on window resize.
      $(window).resize(function() {
        ww = $(window).width();
        draw();
      });

    });
  }
};

// Minify the navigation at mobile resolutions.
Drupal.behaviors.nav = {
  attach: function (context, settings) {
    $('#navigation:not(.nav-processed)', context).addClass('nav-processed').each(function() {

      // Add a helper class to the #navigation div if it [the div]
      // contains content. This helper class is used for styling.
      if ($.trim($(this).html()).length) {
        $(this).addClass('has-content');
      }

      // miniNav().
      function miniNav() {

        // Only add the #nav-togg HTML if the #navigation div
        // contains something to toggle.
        if ($.trim($('#navigation').html()).length) {

          // Translatable text for the #nav-togg label.
          var showText = Drupal.t('Show nav');
          var hideText = Drupal.t('Hide nav');

          // Hide the #navigation div.
          $('#navigation').hide();

          // Insert the #nav-togg HTML.
          $('#navigation').before('<a href="#" id="nav-togg"><span>'+showText+'</span></a>');

          // Handle #nav-togg clicks.
          $('#nav-togg').click(function(){
            $('#navigation').toggle();
            $('span', this).text($('span', this).text() == showText ? hideText : showText);
            return false;
          });
        }
      }

      // If necessary, trigger miniNav().
      // Note: the body:after content is generated via a media query in pages.scss.
      // See: http://adactio.com/journal/5429/ for an explanation.
      var size = window.getComputedStyle(document.body,':after').getPropertyValue('content');
      if (size.indexOf("minw768") == -1) {
        miniNav();
      }

      // On window resize...
      $(window).resize(function() {

        // Retrieve body:after.
        var size = window.getComputedStyle(document.body,':after').getPropertyValue('content');

        // If necessary, trigger miniNav().
        if (size.indexOf("minw768") == -1 && $("#nav-togg").length == 0) {
          miniNav();
        }

        // If necessary, remove #nav-togg and show the #navigation div.
        else if (size.indexOf("minw768") != -1 && $("#nav-togg").length > 0) {
          $('#nav-togg').remove();
          $('#navigation').show();
        }
      });
    });
  }
};

})(jQuery, Drupal, this, this.document);
