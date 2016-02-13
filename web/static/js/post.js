$(document).ready(function(){
    loadComments();
    var dateContainer = $('#post-date span');
    var postDate = new Date(dateContainer.data('iso'));
    //console.log(postDate.getDate());
    dateContainer.text(postDate.toLocaleString());

    function loadComments(){
        $.ajax({
            url: '/feed/api/v1/comments/?postid='+post_id,
            type: 'GET',
            error: function() { },
            success: function(result) {
                if(result.length > 0){
                    var old_comments = $('.card-comment');
                    if(old_comments){
                        old_comments.remove();
                    }
                    var container = $('#comments');
                    var commentTemplate = $('.template-card-comment').clone();
                    commentTemplate.removeClass('template-card-comment');
                    commentTemplate.addClass('card-comment');
                    for(i=0; i<result.length; i++){
                        var comment = commentTemplate.clone();
                        comment.find('.avatar').attr('src', result[i].posted_by.avatar);
                        comment.find('.user-name').text(
                            result[i].posted_by.first_name?
                                result[i].posted_by.first_name
                                    + ' ' + result[i].posted_by.last_name:
                                result[i].posted_by.username
                        );
                        //var modified_at = new Date(result[i].modified_at);
                        comment.find('.timeago-comment').attr('datetime', result[i].modified_at);
                        //comment.find('.time').text(modified_at.toLocaleString());
                        //datetime.timeago();
                        var content = comment.find('.content');
                        content.text(result[i].body);
                        content.smilify();
                        comment.appendTo(container);
                        comment.removeClass('hidden');
                    }
                    $(".timeago-comment").timeago();
                }

            }
        });
    }

    function postComment(){
        $.ajax({
            url: '/feed/api/v1/comments/',
            type: 'POST',
            data:{
                post:post_id,
                body: $("#comment-input").val()
            },
            success: function(e){
                $("#comment-input").val("");
                $("#submit").blur();
                loadComments();
            },
            error: function(e){
                alert(e.responseText);
            }
        });
    }

    $("#comment-form").submit(function(e){
        e.preventDefault();
        if($("#comment-input").val().length > 0){
            postComment();
        } else{
            $("#comment-input").focus();
        }
    });
    function csrfSafeMethod(method) {
        // these HTTP methods do not require CSRF protection
        return (/^(GET|HEAD|OPTIONS|TRACE)$/.test(method));
    }
    $.ajaxSetup({
        beforeSend: function(xhr, settings) {
            if (!csrfSafeMethod(settings.type) && !this.crossDomain) {
                xhr.setRequestHeader("X-CSRFToken", $('[name="csrfmiddlewaretoken"]').val());
            }
        }
    });
});
