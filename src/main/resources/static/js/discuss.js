function like(btn, entityType, entityId,entityUserId,postId) {
    console.log("实体id",entityId);
    console.log("作者id",entityUserId);
    $.post(
        CONTEXT_PATH + "/like",
        {"entityType":entityType,"entityId":entityId,"entityUserId":entityUserId,"postId":postId},
        function(data) {
            console.log("点赞信息",data);
            // data = $.parseJSON(data);
            if(data.code == 0) {
                $(btn).children("i").text(data.data.likeCount);
                $(btn).children("b").text(data.data.likeStatus==1?'已赞':"赞");
            } else {
                alert(data.msg);
            }
        }
    );
}