package com.haruki.poker.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.haruki.poker.repository.UserRepository;
import com.haruki.poker.repository.entity.User;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * 纪录用户信息
     * 
     * @param openid 用户openid
     */
    public User getUserByOpenid(String openid) {
        // 查询用户是否存在
        User user = userRepository.selectByOpenid(openid);
        
        if (user == null) {
            // 新用户，插入记录
            user = new User();
            user.setOpenid(openid);
            user.setNickname(generateRandomNickname());
            userRepository.insert(user);
        }

        return user;
    }

    /**
     * 修改用户昵称
     * @param openid 用户openid
     * @param nickname 新昵称
     * @throws Exception 当用户不存在时抛出异常
     */
    public void updateNickname(String openid, String nickname) throws Exception {
        User user = userRepository.selectByOpenid(openid);
        if (user == null) {
            throw new Exception("用户不存在");
        }
        
        user.setNickname(nickname);
        int result = userRepository.update(user);
        if (result != 1) {
            throw new Exception("更新失败");
        }
    }

    /**
     * 随机生成用户姓名
     */
    private String generateRandomNickname(){
        String[] nicknames = {
            "扑克王者", "德州大师", "扑克战神", "River终结者", "Flop猎手", 
            "All-in之王", "Bluff专家", "Pocket Aces", "牌桌风暴", "扑克大魔王", 
            "德州铁王座", "小盲大赢家", "牌局预言家", "Royal Flush", "Turn奇迹", 
            "四条之神", "扑克教父", "Ace High", "德州扑克皇", "筹码收割机", 
            "诈唬大师", "读心术玩家", "Checkmate", "扑克狂徒", "Straight King", 
            "Flush猎人", "筹码猎手", "River杀手", "牌局掌控者", "扑克哲人", 
            "小盲狙击手", "Full House", "德州终极手", "Heads-up王者", "筹码风暴", 
            "Bad Beat之王", "All-in无敌手", "德州智者", "River翻盘", "Bluff达人", 
            "无敌Flop手", "德州神童", "牌局分析师", "筹码霸主", "High Stakes", 
            "德州奇才", "读心术高手", "River奇迹", "Slow Play大师", "扑克猎人", 
            "筹码富翁", "All-in战神", "Flop奇迹", "德州心算家", "扑克预言者", 
            "筹码帝国", "Bluff无影手", "筹码国王", "德州扑克皇帝", "现金局杀手", 
            "Heads-Up狂人", "筹码专家", "Turn翻盘王", "筹码海洋", "扑克精算师", 
            "Poker Genius", "Royal之魂", "All-in信仰", "德州扑克天才", "筹码暴君", 
            "Bluff艺术家", "牌局掌门人", "Straight Flush", "德州黑马", "筹码守护者", 
            "River翻转手", "筹码战士", "Pocket Kings", "Flop奇兵", "诈唬王者", 
            "德州扑克灵魂", "筹码之巅", "扑克风云", "高额桌猎手", "Bluff智者", 
            "现金局专家", "牌局狙击手", "筹码之神", "德州老手", "River超神", 
            "All-in达人", "扑克魔术师", "筹码狂人", "德州扑克王者", "小盲奇迹", 
            "筹码守护神", "Final Table", "德扑克霸主", "Poker Shark", "扑克之王"
        };
        
        int randomIndex = (int)(Math.random() * nicknames.length);
        return nicknames[randomIndex];
    }
}
