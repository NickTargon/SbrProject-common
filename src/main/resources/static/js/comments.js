$('.li-modal').on('click', function(e){
    e.preventDefault();
    $('#modalDialog').modal('show').find('.modal-content').load($(this).attr('href'));
});

