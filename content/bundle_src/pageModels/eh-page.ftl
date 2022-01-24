<#assign wp=JspTaglibs["/aps-core"]>
<@wp.info key="systemParam" paramName="applicationBaseURL" var="appUrl" />
<html lang="en">
 <head>
    <meta charset="utf-8"/>
    <title>
       <@wp.currentPage param="title" />
    </title>
    <meta name="viewport" content="width=device-width,  user-scalable=no"/>
    <@wp.fragment code="eh_header_inclusion" escapeXml=false />
    <@wp.fragment code="eh_keycloak_auth" escapeXml=false />
 </head>
 <body>
    <div class="CatalogPageWrapper">
       <div class="CatalogPageHeader">
          <div class="CatalogPageMustheadContainer">
             <div class="CatalogPageLogo ">
                <@wp.show frame=0 />
             </div>
             <div class="CatalogPageLogin">
                <@wp.show frame=1 />
             </div>
          </div>
          <div class="bx--col-lg-16">
             <@wp.show frame=2 />
          </div>
       </div>
       <div class="CatalogPageContent">
          <div class="bx--col-lg-16">
             <@wp.show frame=3 />
          </div>
          <div class="bx--col-lg-16">
             <@wp.show frame=4 />
          </div>
       </div>
       <div class="CatalogPageFooterWrapper">
          <@wp.show frame=5 />
       </div>
    </div>
    <script nonce="<@wp.cspNonce />" type="text/javascript" >
       let lastUrl = location.href;
       new MutationObserver(() => {
           const url = location.href;
           if (url !== lastUrl) {
               lastUrl = url;
               onUrlChange();
           }
           else {
            if ((url.indexOf("admin") > -1) || (url.indexOf("bundlegroup") > -1) || (url.indexOf("orgs") > -1)) {
               document.getElementsByClassName('CatalogPageHeader-header-content')[0].style.display = 'none';
           }
           }
       }).observe(document, {
           subtree: true,
           childList: true
       });

       function onUrlChange() {
           var url = window.location.href;
           if ((url.indexOf("admin") > -1) || (url.indexOf("bundlegroup") > -1)) {
               document.getElementsByClassName('CatalogPageHeader-header-content')[0].style.display = 'none';
           } else {
               document.getElementsByClassName('CatalogPageHeader-header-content')[0].style.display = 'block';
           }
       }
    </script>
 </body>
</html>