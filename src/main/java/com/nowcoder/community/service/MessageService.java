package com.nowcoder.community.service;

import com.nowcoder.community.entity.Message;

import java.util.List;

public interface MessageService {

    /**
     * 会话列表：和一堆人的会话，但是是双方的，需要另外判断收件人
     * @param userId 收件/发件
     * @param offset
     * @param limit
     * @return
     */
    public List<Message> findConversations(int userId, int offset, int limit);

    /**
     * 会话总数
     * @param userId 收件/发件
     * @return
     */
    public int findConversationCount(int userId);

    /**
     * 一个会话的私信：和某人的私信，但是是双方的，需要另外判断收件人
     * @param conversationId
     * @param offset
     * @param limit
     * @return
     */
    public List<Message> findLetters(String conversationId, int offset, int limit);

    /**
     * 私信总数
     * @param conversationId
     * @return
     */
    public int findLetterCount(String conversationId);

    /**
     * 未读私信总数
     * @param userId 被发信息的用户，才有未读
     * @param conversationId
     * @return
     */
    public int findLetterUnreadCount(int userId, String conversationId);


    int insertMessage(Message message);

    int updateStatus(List<Integer> ids);
}
