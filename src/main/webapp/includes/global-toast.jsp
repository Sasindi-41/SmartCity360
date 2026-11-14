<div id="globalToast" class="global-toast" role="status" aria-live="polite" hidden>
  <span class="global-toast-icon" id="globalToastIcon">✓</span>
  <span id="globalToastMessage"></span>
  <button type="button" class="global-toast-close" aria-label="Dismiss notification" onclick="hideGlobalToast()">×</button>
</div>
<script>
  (function(){
    let timer=null;
    window.showGlobalToast=function(message,type){
      const toast=document.getElementById('globalToast');
      const text=document.getElementById('globalToastMessage');
      const icon=document.getElementById('globalToastIcon');
      if(!toast||!text||!icon)return;
      toast.className='global-toast '+(type||'success');
      icon.textContent=(type==='error'?'!':type==='warning'?'!':'✓');
      text.textContent=message||'Changes saved successfully.';
      toast.hidden=false;
      if(timer)clearTimeout(timer);
      timer=setTimeout(window.hideGlobalToast,4200);
    };
    window.hideGlobalToast=function(){const toast=document.getElementById('globalToast');if(toast)toast.hidden=true;if(timer){clearTimeout(timer);timer=null;}};
  })();
</script>
