<#import "window.ftl" as win>
<#import "form.ftl" as fo>
Ext.onReady(function() {
<@fo.formModel form></@fo.formModel>
<@win.windowModel window></@win.windowModel>
});