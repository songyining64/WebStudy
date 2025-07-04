<template>
  <div>
    <div class="home-bg"></div>
    <div class="home-main">
      <!-- 轮播图区域 -->
      <div class="overlap-carousel-area">
        <div class="overlap-carousel-wrapper">
          <div class="overlap-carousel-track">
            <div
              v-for="(img, idx) in images"
              :key="img"
              class="overlap-carousel-img"
              :class="{ active: idx === hoverIndex }"
              @mouseenter="handleCarouselMouseEnter(idx)"
              @mouseleave="handleCarouselMouseLeave"
              @click="showDetail(idx)"
            >
              <img :src="getImgUrl(img)" :alt="`轮播图${idx+1}`" />
            </div>
          </div>
        </div>
      </div>

      <!-- 音乐播放器在推文上方 -->
      <div class="music-player music-player-top">
        <div class="music-info">
          <div class="cover-wrapper">
            <img :src="currentCover" class="cover-img" />
          </div>
          <div class="music-meta">
            <div class="music-title">{{ musicList[currentIndex].title }}</div>
            <div class="music-artist">{{ musicList[currentIndex].artist || '未知艺术家' }}</div>
            <div class="time-display">
              <span class="current-time">{{ formatTime(currentTime) }}</span>
              <span class="total-time">{{ formatTime(duration) }}</span>
            </div>
            <div class="progress-bar-bg" @click="seekTo">
              <div class="progress-bar" :style="{ width: progressPercent + '%' }"></div>
              <div class="progress-handle" :style="{ left: progressPercent + '%' }"></div>
            </div>
          </div>
        </div>
        <div class="music-controls">
          <button @click="prevMusic" class="music-btn" title="上一首">
            <span>&#9664;&#9664;</span>
          </button>
          <button @click="togglePlay" class="music-btn play-btn" title="播放/暂停">
            <span v-if="!isPlaying">&#9654;</span>
            <span v-else>||</span>
          </button>
          <button @click="nextMusic" class="music-btn" title="下一首">
            <span>&#9654;&#9654;</span>
          </button>
        </div>
        <div class="music-extra-controls">
          <div class="play-mode-control">
            <button @click="togglePlayMode" class="mode-btn" :title="playModeText">
              <span v-if="playMode === 'loop'">🔁</span>
              <span v-else-if="playMode === 'shuffle'">🔀</span>
              <span v-else>🔂</span>
            </button>
      </div>
          <div class="volume-control">
            <button @click="toggleMute" class="volume-btn" :title="isMuted ? '取消静音' : '静音'">
              <span v-if="isMuted">🔇</span>
              <span v-else-if="volume < 0.5">🔉</span>
              <span v-else>🔊</span>
            </button>
            <div class="volume-slider" @click="setVolume">
              <div class="volume-bar">
                <div class="volume-fill" :style="{ width: (isMuted ? 0 : volume * 100) + '%' }"></div>
              </div>
            </div>
          </div>
          <div class="playlist-control">
            <button @click="togglePlaylist" class="playlist-btn" title="播放列表">
              <span>📋</span>
            </button>
          </div>
        </div>
        <audio 
          ref="audio" 
          :src="musicList[currentIndex].src" 
          @timeupdate="onTimeUpdate" 
          @ended="onEnded"
          @loadedmetadata="onLoadedMetadata"
          @error="onAudioError"
          preload="metadata"
        />
      </div>

      <!-- 播放列表弹窗 -->
      <div class="playlist-modal" v-if="showPlaylist" @click.self="togglePlaylist">
        <div class="playlist-content">
          <div class="playlist-header">
            <h3>播放列表 ({{ musicList.length }})</h3>
            <button @click="togglePlaylist" class="close-playlist-btn">&times;</button>
          </div>
          <div class="playlist-items">
            <div 
              v-for="(song, index) in musicList" 
              :key="index"
              :class="['playlist-item', { active: index === currentIndex }]"
              @click="playSong(index)"
            >
              <div class="playlist-item-info">
                <img :src="song.cover" class="playlist-item-cover" />
                <div class="playlist-item-details">
                  <div class="playlist-item-title">{{ song.title }}</div>
                  <div class="playlist-item-artist">{{ song.artist || '未知艺术家' }}</div>
                </div>
              </div>
              <div class="playlist-item-status">
                <span v-if="index === currentIndex && isPlaying" class="playing-indicator">▶</span>
                <span v-else-if="index === currentIndex" class="paused-indicator">⏸</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 推文区域 -->
      <div class="post-area post-area-top">
        <div class="post-card">
          <h2>简介</h2>
          <pre class="post-pre">
情波港：情绪舒缓的爱心港湾
引言：情绪舒缓的时代需求
      在快节奏的现代生活中，焦虑、孤独、压力成为普遍的社会情绪症结。据世界卫生组织统计，全球超3亿人受情绪问题困扰，而传统心理咨询资源稀缺、成本高昂。情波港（Emotion Harbor）应运而生——一个专注于情绪舒缓的公益网页平台，通过"聊天机器人即时支持 + 精准资源推荐 + 社区互助交流"三维体系，为大众提供温暖、高效的"情绪急救站"。它不仅是工具，更是一座传递爱心的桥梁。
一、聊天机器人：全天候的"情绪急救员"
       情波港的智能聊天机器人（昵称"小情"）是平台核心，运用情感计算与自然语言处理技术，实现人性化情绪疏导：
1.即时倾听与共情响应
       场景示例：用户深夜输入"最近工作压力大到失眠"，小情会先回应："听起来你正承受很大的负担，失眠一定很煎熬吧？"（共情），而非机械式提问。
2.技术支撑：
       通过语义分析识别情绪关键词（如"压力""孤独"），触发预设的舒缓策略库，如引导呼吸练习（"跟我一起深呼吸三次好吗？"）。
3.个性化疏导方案
根据对话深度动态调整：
初级焦虑：推荐5分钟正念音频；
长期抑郁：生成阶段性疏导计划（如"每日情绪日记+微目标打卡"）。
4.爱心救助案例：
        一位产后抑郁妈妈通过21天机器人陪伴，逐步重建生活节奏，最终在社区分享："小情像深夜的一盏灯"。
5.危机干预转接机制
       当识别自杀倾向等高风险语句时，自动连接公益心理热线，并推送本地援助资源（如"xx市24小时心理救援中心"），实现"AI-人工"无缝接力。
聊天机器人以零门槛、零评判的特性，打破时空限制，成为触手可及的"情绪避风港"。
二、资源推荐系统：定制化的"情绪药箱"
       情波港整合千余项舒缓资源，通过算法匹配用户需求，实现"精准治愈"：
1.智能匹配逻辑
短期需求：若用户描述"考试前心慌"，系统优先推送"考前放松呼吸指南"短视频；
长期建设：对频繁登录的孤独用户，逐步推荐"社交技能提升"课程与线下活动。
数据反馈：用户评分高的资源自动加权推荐，形成"群体智慧"筛选机制。
2.爱心传递设计
资源共创：用户可上传自创内容（如一首安慰诗），经审核后标注"来自@匿名伙伴的礼物"；
紧急包功能：针对灾害事件（如地震后），自动推送"集体创伤应对指南"并开通定向捐赠通道。
       资源系统像一位"懂你的药剂师"，将碎片化信息转化为结构化治愈力。
三、社区交流：温情的"情绪共生圈"
      情波港建立"树洞社区"，以严格规则守护安全空间，激发用户间的情感互助：
1.分层交流机制
匿名树洞区：完全加密发帖，用户倾诉压力获得共鸣回应（如"我也曾失业，3个月后找到转机"）；
主题互助组：按需求细分群组（"单亲父母情绪角""抗癌者联盟"），降低孤独感；
专家互动日：每月邀请心理咨询师在线答疑，公益科普情绪知识。
2.爱心救助的社区化实践
故事银行：用户记录情绪康复历程，如"从恐慌症到登山者"的自述，给予他人希望；
微行动倡议：发起"今天夸1个人"挑战，参与者上传对话截图，累计完成超10万次正向互动；
紧急支援网：用户可标记"急需倾听"，志愿者团队（认证用户）15分钟内响应，形成"人机协作"救援网。
3.安全守护与正向引导
AI+人工审核：屏蔽攻击性言论，对负面帖子触发关怀私信（"需要和小情聊聊吗？"）；
能量值体系：积极互动获"爱心积分"，兑换公益周边（印有用户留言的治愈手账本）。
      案例：一名大学生在社区发帖"想休学但不敢说"，收到32条经验分享后决定与家人沟通，后续回帖："这里让我知道，脆弱也是力量。"
      情波港——从舒缓到共生的爱心革命，情波港的创新在于将科技温度与人性关怀深度融合：
聊天机器人提供"即时安全感"，资源系统赋予"自我疗愈力"，社区培育"集体希望感"，三者形成情绪救助闭环；其公益属性打破经济壁垒——所有功能免费开放，运营依靠社会捐赠与志愿者网络，真正践行"情绪平权"。
在情波港，每一次对话、一次资源打开、一条社区回复，都是爱心的具体形态。正如一位用户留言：
"这里没有神药，但有无数双手在深渊边拉住你。"情绪舒缓之路，终因共行而不孤独。
          </pre>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getRecentNotices, getSystemNotices, getNoticeDetail } from '@/api/communityApi';
import { Bell, Document, Clock } from '@element-plus/icons-vue'

export default {
  name: 'Home',
  components: {
    Bell,
    Document,
    Clock
  },
  data() {
    return {
      images: [
        'lunhuan1.jpg',
        'lunhuan2.jpg',
        'lunhuan3.jpg',
        'lunhuan4.jpg',
        'lunhuan5.jpg'
      ],
      hoverIndex: null,
      showModal: false,
      currentDetailIndex: 0,
      autoCarouselTimer: null,
      imageDetails: [
        {
          title: "打工人职场情绪急救包",
          content: `
            <p class="detail-subtitle">为职场人士提供及时的情绪支持，让你在工作中保持积极健康的心态</p>
            <div class="detail-section">
              <h4>主要功能</h4>
              <ul>
                <li>快速情绪减压技巧：3-5分钟即可完成的呼吸练习和放松方法</li>
                <li>工作压力应对指南：包含会议紧张、截止日期压力、团队冲突等常见场景的解决方案</li>
                <li>职场人际关系建议：如何与上级沟通、处理同事关系、应对职场政治等实用建议</li>
                <li>工作倦怠预防方案：识别倦怠早期信号，建立健康的工作界限，维持工作热情</li>
                <li>职业发展规划：帮助你在压力中找到职业方向，制定合理的职业目标</li>
              </ul>
            </div>
            <div class="detail-section">
              <h4>使用场景</h4>
              <p>当你感到工作压力大、与同事沟通不畅、项目deadline临近时，打开急救包，找到适合你的解压方式。我们为不同场景提供了详细的应对策略：</p>
              <ul>
                <li>会议前的紧张感：通过快速呼吸练习和积极自我对话缓解压力</li>
                <li>工作量突然增加：运用时间管理技巧，合理分配任务优先级</li>
                <li>团队合作不顺：使用有效沟通模板，化解团队矛盾</li>
                <li>加班疲惫时：采用能量管理方法，保持工作效率</li>
              </ul>
            </div>
            <div class="detail-section">
              <h4>专业建议</h4>
              <p>建立健康的工作边界，学会合理分配时间，在高压工作中保持心理健康。具体可以：</p>
              <ul>
                <li>每天留出15分钟的独处时间，进行心理减压</li>
                <li>学会说"不"，避免过度承诺</li>
                <li>建立工作日程表，保持生活规律</li>
                <li>培养工作之外的兴趣爱好，平衡生活</li>
              </ul>
              <p>记住：工作重要，但自己的身心健康更重要。合理的压力能够提升工作效率，过度的压力则会适得其反。</p>
            </div>
          `
        },
        {
          title: "5分钟呼吸魔法",
          content: `
            <p class="detail-subtitle">简单有效的减压放松方法，让你随时随地找回内心的平静</p>
            <div class="detail-section">
              <h4>呼吸练习基础知识</h4>
              <p>呼吸是连接身心的桥梁，通过调节呼吸可以有效缓解压力、焦虑和紧张情绪。科学研究表明，正确的呼吸方式能够：</p>
              <ul>
                <li>降低血压和心率，缓解身体紧张</li>
                <li>增加血液含氧量，提升大脑清晰度</li>
                <li>激活副交感神经系统，促进身心放松</li>
                <li>改善情绪状态，提升幸福感</li>
              </ul>
            </div>
            <div class="detail-section">
              <h4>呼吸练习步骤</h4>
              <ul>
                <li>找到安静舒适的环境，可以是办公室、家中或户外</li>
                <li>采用4-7-8呼吸法：吸气4秒，屏气7秒，呼气8秒</li>
                <li>配合轻柔的背景音乐，增强放松效果</li>
                <li>觉察身体的放松感，感受每一次呼吸带来的平静</li>
                <li>保持正确的姿势：脊背挺直，肩膀放松，双手自然放置</li>
              </ul>
              <p>进阶练习方法：</p>
              <ul>
                <li>腹式呼吸：将注意力集中在腹部的起伏上</li>
                <li>方块呼吸：吸气4秒，屏气4秒，呼气4秒，屏气4秒</li>
                <li>意念引导：在呼吸时想象平静的场景</li>
              </ul>
            </div>
            <div class="detail-section">
              <h4>适用情况</h4>
              <p>这套呼吸练习方法适用于多种场景：</p>
              <ul>
                <li>焦虑紧张时：快速平复紧张情绪</li>
                <li>失眠前：帮助身心放松，促进睡眠</li>
                <li>重要会议前：提升专注力和表现</li>
                <li>情绪波动时：重新找回平静</li>
                <li>工作压力大时：缓解压力，恢复能量</li>
              </ul>
            </div>
            <div class="detail-section">
              <h4>注意事项</h4>
              <p>为了获得最佳效果，请注意以下几点：</p>
              <ul>
                <li>保持规律练习，让身心逐渐适应这种放松方式</li>
                <li>不要强求完美，循序渐进地增加练习时长</li>
                <li>如果感到不适，可以随时调整呼吸节奏</li>
                <li>建议早晚各练习一次，形成良好习惯</li>
              </ul>
              <p>记住：呼吸练习是一种简单但强大的自我调节工具，持之以恒必将收获平静与力量。</p>
            </div>
          `
        },
        {
          title: "每日心情温度计",
          content: `
            <p class="detail-subtitle">记录和关注你的情绪变化，培养情绪觉察能力，建立健康的情绪管理模式</p>
            <div class="detail-section">
              <h4>为什么要记录情绪？</h4>
              <p>情绪记录不仅仅是简单的记录，更是一种自我认知和成长的工具：</p>
              <ul>
                <li>帮助识别情绪触发因素，预防情绪失控</li>
                <li>发现情绪模式，提前做好准备</li>
                <li>培养情绪觉察能力，提升情商</li>
                <li>追踪情绪变化，评估心理健康状态</li>
                <li>为寻求专业帮助提供详实的参考数据</li>
              </ul>
            </div>
            <div class="detail-section">
              <h4>情绪记录方式</h4>
              <p>我们提供科学的情绪记录方法：</p>
              <ul>
                <li>选择当前心情指数（1-10分）：
                  <ul>
                    <li>1-3分：情绪低落，需要关注</li>
                    <li>4-6分：情绪平稳，状态一般</li>
                    <li>7-10分：情绪愉悦，状态良好</li>
                  </ul>
                </li>
                <li>记录情绪触发事件：详细描述引发情绪的具体情境</li>
                <li>写下情绪感受：用具体的词汇描述你的感受</li>
                <li>身体感觉记录：注意情绪带来的身体反应</li>
                <li>制定调节计划：针对性地选择调节方法</li>
              </ul>
            </div>
            <div class="detail-section">
              <h4>使用建议</h4>
              <p>为了获得最佳效果，我们建议：</p>
              <ul>
                <li>每天固定时间记录，培养习惯</li>
                <li>选择安静的环境，保持专注</li>
                <li>诚实面对自己的情绪，不要回避负面感受</li>
                <li>定期回顾记录，总结规律</li>
                <li>遇到持续的负面情绪时，及时寻求帮助</li>
              </ul>
              <p>情绪记录的进阶技巧：</p>
              <ul>
                <li>使用情绪词汇表，丰富情绪表达</li>
                <li>结合生活事件，分析情绪成因</li>
                <li>记录应对策略的效果</li>
                <li>建立情绪支持网络</li>
              </ul>
            </div>
            <div class="detail-section">
              <h4>长期效果</h4>
              <p>坚持情绪记录将带来以下改变：</p>
              <ul>
                <li>提升情绪觉察能力，更了解自己</li>
                <li>建立健康的情绪管理习惯</li>
                <li>预防情绪危机，维护心理健康</li>
                <li>改善人际关系，提升沟通质量</li>
                <li>增强心理韧性，应对生活挑战</li>
              </ul>
              <p>记住：了解自己的情绪是心理健康的第一步，让我们一起开启情绪觉察之旅。</p>
            </div>
          `
        },
        {
          title: "心理咨询空间",
          content: `
            <p class="detail-subtitle">专业的心理支持服务，为你的心理健康保驾护航</p>
            <div class="detail-section">
              <h4>我们的优势</h4>
              <p>专业、安全、保密的心理咨询服务：</p>
              <ul>
                <li>资深咨询师团队：
                  <ul>
                    <li>持有国家认证心理咨询师资质</li>
                    <li>具备丰富的实践经验</li>
                    <li>定期接受专业督导和培训</li>
                    <li>擅长处理各类心理问题</li>
                  </ul>
                </li>
                <li>严格的保密制度：
                  <ul>
                    <li>完善的隐私保护机制</li>
                    <li>安全的数据存储系统</li>
                    <li>专业的伦理道德规范</li>
                  </ul>
                </li>
              </ul>
            </div>
            <div class="detail-section">
              <h4>服务内容</h4>
              <p>我们提供全方位的心理健康服务：</p>
              <ul>
                <li>一对一心理咨询：
                  <ul>
                    <li>情绪管理咨询</li>
                    <li>人际关系辅导</li>
                    <li>职业发展咨询</li>
                    <li>婚恋情感咨询</li>
                    <li>家庭关系辅导</li>
                  </ul>
                </li>
                <li>团体心理辅导：
                  <ul>
                    <li>情绪管理工作坊</li>
                    <li>人际沟通训练</li>
                    <li>压力管理课程</li>
                    <li>自我成长小组</li>
                  </ul>
                </li>
                <li>心理健康评估：
                  <ul>
                    <li>人格特质测评</li>
                    <li>情绪状态评估</li>
                    <li>压力水平测试</li>
                    <li>人际关系评估</li>
                  </ul>
                </li>
                <li>个性化干预方案：
                  <ul>
                    <li>基于评估结果定制</li>
                    <li>结合个人需求调整</li>
                    <li>定期评估和调整</li>
                    <li>全程专业指导</li>
                  </ul>
                </li>
              </ul>
            </div>
            <div class="detail-section">
              <h4>咨询方式</h4>
              <p>为满足不同需求，我们提供多种咨询方式：</p>
              <ul>
                <li>线下面对面咨询：
                  <ul>
                    <li>舒适的咨询环境</li>
                    <li>灵活的预约时间</li>
                    <li>专业的咨询设备</li>
                  </ul>
                </li>
                <li>线上远程咨询：
                  <ul>
                    <li>视频咨询</li>
                    <li>语音咨询</li>
                    <li>文字咨询</li>
                  </ul>
                </li>
              </ul>
              <p>所有咨询方式均确保隐私安全，你可以选择最适合自己的方式。</p>
            </div>
            <div class="detail-section">
              <h4>服务流程</h4>
              <p>规范的服务流程保证咨询质量：</p>
              <ul>
                <li>初步评估：了解基本情况和需求</li>
                <li>匹配咨询师：根据专业方向和个性特点</li>
                <li>制定方案：个性化的咨询计划</li>
                <li>开展咨询：专业的心理辅导</li>
                <li>效果评估：定期回访和调整</li>
              </ul>
              <p>我们始终秉持"以人为本"的理念，用专业和温度守护你的心理健康。</p>
            </div>
          `
        },
        {
          title: "情绪垃圾桶",
          content: `
            <p class="detail-subtitle">安全释放负面情绪的空间，让烦恼和压力不再积压在心里</p>
            <div class="detail-section">
              <h4>什么是情绪垃圾桶？</h4>
              <p>情绪垃圾桶是一个特殊的心理空间，它的设计理念是：</p>
              <ul>
                <li>提供安全的情绪宣泄渠道</li>
                <li>帮助摆脱负面情绪的困扰</li>
                <li>转化消极能量为积极力量</li>
                <li>保护个人隐私和情感安全</li>
              </ul>
              <p>就像处理生活垃圾一样，我们也需要一个地方来处理情绪垃圾，让心灵保持整洁。</p>
            </div>
            <div class="detail-section">
              <h4>功能特点</h4>
              <p>我们提供多种情绪释放方式：</p>
              <ul>
                <li>匿名情绪倾诉：
                  <ul>
                    <li>完全匿名的表达环境</li>
                    <li>不用担心被评判</li>
                    <li>随时随地可以倾诉</li>
                    <li>多种表达形式选择</li>
                  </ul>
                </li>
                <li>负面情绪转化：
                  <ul>
                    <li>引导式情绪疏导</li>
                    <li>正念减压练习</li>
                    <li>情绪能量转化技巧</li>
                    <li>积极思维重构</li>
                  </ul>
                </li>
                <li>情绪能量释放：
                  <ul>
                    <li>虚拟撕纸发泄</li>
                    <li>情绪宣泄写作</li>
                    <li>声音情绪释放</li>
                    <li>创意艺术表达</li>
                  </ul>
                </li>
                <li>积极反馈机制：
                  <ul>
                    <li>情绪释放后的正向提示</li>
                    <li>鼓励性的回应系统</li>
                    <li>成长性的反思建议</li>
                    <li>持续的支持和关怀</li>
                  </ul>
                </li>
              </ul>
            </div>
            <div class="detail-section">
              <h4>使用方法</h4>
              <p>简单的三步骤，帮你卸下情绪包袱：</p>
              <ul>
                <li>第一步：识别情绪
                  <ul>
                    <li>觉察当前的情绪状态</li>
                    <li>找到情绪的来源</li>
                    <li>了解情绪的强度</li>
                  </ul>
                </li>
                <li>第二步：表达情绪
                  <ul>
                    <li>选择适合的表达方式</li>
                    <li>真实地表达感受</li>
                    <li>不加过滤地倾诉</li>
                  </ul>
                </li>
                <li>第三步：转化情绪
                  <ul>
                    <li>接纳和理解自己的情绪</li>
                    <li>寻找积极的应对方式</li>
                    <li>制定改善计划</li>
                  </ul>
                </li>
              </ul>
            </div>
            <div class="detail-section">
              <h4>温馨提示</h4>
              <p>使用情绪垃圾桶时，请记住：</p>
              <ul>
                <li>这是一个安全的倾诉空间，所有内容都会得到保护</li>
                <li>表达负面情绪是正常的，不要自责</li>
                <li>适度宣泄有助于心理健康</li>
                <li>如果情绪持续低落，建议寻求专业帮助</li>
              </ul>
              <p>让我们一起学会和情绪和平相处，创造更健康的心理状态。</p>
            </div>
          `
        },
        {
          title: "生活重建指南",
          content: `
            <p class="detail-subtitle">帮助你重拾生活的掌控感，建立积极健康的生活方式</p>
            <div class="detail-section">
              <h4>为什么需要生活重建？</h4>
              <p>生活重建不仅仅是改变生活习惯，更是一次自我提升的旅程：</p>
              <ul>
                <li>找回生活的意义和方向</li>
                <li>建立健康的生活节奏</li>
                <li>提升生活质量和幸福感</li>
                <li>培养积极的生活态度</li>
                <li>实现个人成长和发展</li>
              </ul>
            </div>
            <div class="detail-section">
              <h4>核心内容</h4>
              <p>我们提供全面的生活重建方案：</p>
              <ul>
                <li>生活规划指导：
                  <ul>
                    <li>价值观梳理和确立</li>
                    <li>生活目标设定</li>
                    <li>时间管理策略</li>
                    <li>生活平衡维护</li>
                  </ul>
                </li>
                <li>习惯养成建议：
                  <ul>
                    <li>早睡早起的作息调整</li>
                    <li>健康饮食的习惯培养</li>
                    <li>运动习惯的建立</li>
                    <li>学习成长的方法指导</li>
                  </ul>
                </li>
                <li>目标设定方法：
                  <ul>
                    <li>SMART原则应用</li>
                    <li>阶段性目标规划</li>
                    <li>行动计划制定</li>
                    <li>目标调整和优化</li>
                  </ul>
                </li>
                <li>行动计划制定：
                  <ul>
                    <li>具体可行的执行步骤</li>
                    <li>时间节点的确定</li>
                    <li>资源调配建议</li>
                    <li>应对策略准备</li>
                  </ul>
                </li>
              </ul>
            </div>
            <div class="detail-section">
              <h4>实施步骤</h4>
              <p>生活重建是一个循序渐进的过程：</p>
              <ul>
                <li>第一阶段：评估和规划
                  <ul>
                    <li>现状评估</li>
                    <li>目标设定</li>
                    <li>计划制定</li>
                  </ul>
                </li>
                <li>第二阶段：行动和调整
                  <ul>
                    <li>执行计划</li>
                    <li>记录过程</li>
                    <li>及时调整</li>
                  </ul>
                </li>
                <li>第三阶段：维护和提升
                  <ul>
                    <li>习惯巩固</li>
                    <li>效果评估</li>
                    <li>持续优化</li>
                  </ul>
                </li>
              </ul>
            </div>
            <div class="detail-section">
              <h4>应用场景</h4>
              <p>生活重建指南适用于多种情况：</p>
              <ul>
                <li>生活失序，需要重新规划</li>
                <li>工作转换，适应新环境</li>
                <li>人生低谷，寻求改变</li>
                <li>目标迷失，重新定位</li>
                <li>习惯不良，期待改善</li>
              </ul>
            </div>
            <div class="detail-section">
              <h4>预期效果</h4>
              <p>通过系统性的指导，你将获得：</p>
              <ul>
                <li>更清晰的生活目标</li>
                <li>更健康的生活方式</li>
                <li>更高效的时间管理</li>
                <li>更积极的生活态度</li>
                <li>更强的执行力</li>
              </ul>
              <p>记住：每一个微小的改变都是迈向更好生活的一步，让我们一起开启生活重建之旅。</p>
            </div>
          `
        }
      ],
      musicList: [
        {
          title: 'Moon Night',
          artist: '夜月',
          src: new URL('../assets/moonnight.mp3', import.meta.url).href,
          cover: new URL('../assets/lunhuan1.jpg', import.meta.url).href
        },
        {
          title: '雨的印记',
          artist: '雨声',
          src: new URL('../assets/雨的印记.mp3', import.meta.url).href,
          cover: new URL('../assets/lunhuan2.jpg', import.meta.url).href
        },
        {
          title: '春江花月夜',
          artist: '古典音乐',
          src: new URL('../assets/chunjianghuayyueye.mp3', import.meta.url).href,
          cover: new URL('../assets/lunhuan3.jpg', import.meta.url).href
        },
        {
          title: '好事要发生',
          artist: '轻音乐',
          src: new URL('../assets/haoshiyaofasheng.mp3', import.meta.url).href,
          cover: new URL('../assets/lunhuan4.jpg', import.meta.url).href
        },
        {
          title: 'Gotta Be A Reason',
          artist: '英文歌曲',
          src: new URL('../assets/《Gotta Be A Reason》.mp3', import.meta.url).href,
          cover: new URL('../assets/lunhuan5.jpg', import.meta.url).href
        }
      ],
      currentIndex: 0,
      isPlaying: false,
      progressPercent: 0,
      currentTime: 0,
      duration: 0,
      playMode: 'loop',
      isMuted: false,
      volume: 0.5,
      showPlaylist: false,
      notices: [],
      showNoticeDetail: false,
      currentNotice: null,
      showAllNoticesDialog: false,
      allNotices: [],
      noticeTotal: 0,
      noticePage: 1,
      noticePageSize: 10,
      noticesLoading: false
    }
  },
  created() {
    this.startAutoCarousel();
    this.fetchRecentNotices();
  },
  computed: {
    currentCover() {
      return this.musicList[this.currentIndex].cover;
    },
    playModeText() {
      return this.playMode === 'loop' ? '循环播放' : this.playMode === 'shuffle' ? '随机播放' : '单曲循环';
    }
  },
  watch: {
    currentIndex(newVal, oldVal) {
      // 切换歌曲后，等待src变更后再播放，避免AbortError
      this.$nextTick(() => {
        const audio = this.$refs.audio;
        audio.currentTime = 0;
        const playPromise = audio.play();
        if (playPromise && playPromise.catch) {
          playPromise.catch(() => {});
        }
        this.isPlaying = true;
      });
    }
  },
  mounted() {
    this.startAutoCarousel();
  },
  beforeUnmount() {
    this.clearAutoCarousel();
  },
  methods: {
    getImgUrl(img) {
      return new URL(`../assets/${img}`, import.meta.url).href;
    },
    formatTime(seconds) {
      if (isNaN(seconds)) return '0:00';
      const mins = Math.floor(seconds / 60);
      const secs = Math.floor(seconds % 60);
      return `${mins}:${secs.toString().padStart(2, '0')}`;
    },
    prevMusic() {
      if (this.playMode === 'shuffle') {
        this.currentIndex = Math.floor(Math.random() * this.musicList.length);
      } else {
      this.currentIndex = (this.currentIndex - 1 + this.musicList.length) % this.musicList.length;
      }
    },
    nextMusic() {
      if (this.playMode === 'shuffle') {
        this.currentIndex = Math.floor(Math.random() * this.musicList.length);
      } else {
      this.currentIndex = (this.currentIndex + 1) % this.musicList.length;
      }
    },
    togglePlay() {
      const audio = this.$refs.audio;
      if (this.isPlaying) {
        audio.pause();
      } else {
        audio.play();
      }
      this.isPlaying = !this.isPlaying;
    },
    seekTo(e) {
      const audio = this.$refs.audio;
      const rect = e.target.getBoundingClientRect();
      const clickX = e.clientX - rect.left;
      const percentage = clickX / rect.width;
      audio.currentTime = percentage * audio.duration;
    },
    onTimeUpdate(e) {
      const audio = e.target;
      this.progressPercent = (audio.currentTime / audio.duration) * 100;
      this.currentTime = audio.currentTime;
      this.duration = audio.duration;
    },
    onEnded() {
      if (this.playMode === 'single') {
        // 单曲循环，重新播放当前歌曲
        this.$nextTick(() => {
          const audio = this.$refs.audio;
          audio.currentTime = 0;
          audio.play();
        });
      } else {
        // 循环播放或随机播放，播放下一首
        this.nextMusic();
      }
    },
    onLoadedMetadata(e) {
      this.duration = e.target.duration;
      // 设置音量
      e.target.volume = this.volume;
    },
    onAudioError(e) {
      console.error('音频加载错误:', e);
    },
    togglePlayMode() {
      const modes = ['loop', 'shuffle', 'single'];
      const currentIndex = modes.indexOf(this.playMode);
      this.playMode = modes[(currentIndex + 1) % modes.length];
    },
    toggleMute() {
      this.isMuted = !this.isMuted;
      const audio = this.$refs.audio;
      if (this.isMuted) {
        audio.volume = 0;
      } else {
        audio.volume = this.volume;
      }
    },
    setVolume(e) {
      const rect = e.target.getBoundingClientRect();
      const clickX = e.clientX - rect.left;
      const percentage = clickX / rect.width;
      this.volume = Math.max(0, Math.min(1, percentage));
      this.isMuted = false;
      
      const audio = this.$refs.audio;
      if (audio) {
        audio.volume = this.volume;
      }
    },
    togglePlaylist() {
      this.showPlaylist = !this.showPlaylist;
    },
    playSong(index) {
      this.currentIndex = index;
      this.showPlaylist = false;
    },
    showDetail(index) {
      this.currentDetailIndex = index;
      this.showModal = true;
      // 添加禁止滚动类
      document.body.classList.add('modal-open');
    },
    closeModal() {
      this.showModal = false;
      // 移除禁止滚动类
      document.body.classList.remove('modal-open');
    },
    startAutoCarousel() {
      this.clearAutoCarousel();
      this.autoCarouselTimer = setInterval(() => {
        if (this.hoverIndex === null) {
          this.hoverIndex = 0;
        } else {
          this.hoverIndex = (this.hoverIndex + 1) % this.images.length;
        }
      }, 3000);
    },
    clearAutoCarousel() {
      if (this.autoCarouselTimer) {
        clearInterval(this.autoCarouselTimer);
        this.autoCarouselTimer = null;
      }
    },
    handleCarouselMouseEnter(idx) {
      this.hoverIndex = idx;
      this.clearAutoCarousel();
    },
    handleCarouselMouseLeave() {
      this.startAutoCarousel();
    },
    showAllNotices() {
      this.noticePage = 1; // 重置页码
      this.showAllNoticesDialog = true;
      // 对话框打开时会通过 @open 事件触发 fetchNotices 方法
    },
    viewNoticeDetail(notice) {
      // 如果已经有完整的公告信息，直接显示
      if (notice.content) {
        this.currentNotice = notice;
        this.showNoticeDetail = true;
        return;
      }
      
      // 否则获取详细信息
      getNoticeDetail(notice.id).then(response => {
        if (response && response.data) {
          this.currentNotice = response.data;
          this.showNoticeDetail = true;
        }
      }).catch(error => {
        console.error('获取公告详情失败:', error);
        this.$message.error('获取公告详情失败');
      });
    },
    noticePageChange(page) {
      this.noticePage = page;
      this.fetchNotices();
    },
    fetchNotices() {
      this.noticesLoading = true;
      const params = {
        page: this.noticePage,
        size: this.noticePageSize
      };
      
      console.log('正在获取系统公告列表，参数:', params);
      
      getSystemNotices(params).then(response => {
        console.log('获取系统公告列表响应:', response);
        if (response && response.data) {
          this.allNotices = response.data.records || response.data;
          this.noticeTotal = response.data.total || response.data.length;
          console.log('加载的公告数量:', this.allNotices.length);
        } else {
          console.warn('获取系统公告列表返回空数据');
          this.allNotices = [];
          this.noticeTotal = 0;
        }
      }).catch(error => {
        console.error('获取公告列表失败:', error);
        // 使用模拟数据
        this.allNotices = [
          {
            id: 'mock1',
            title: '欢迎使用心理健康系统',
            content: '这是一条模拟的系统公告，当API未正常工作时会显示。',
            createTime: new Date().toISOString(),
            updateTime: new Date().toISOString(),
            status: 1
          }
        ];
        this.noticeTotal = this.allNotices.length;
      }).finally(() => {
        this.noticesLoading = false;
      });
    },
    fetchRecentNotices() {
      console.log('正在获取最近系统公告');
      getRecentNotices(5).then(response => {
        console.log('获取最近系统公告响应:', response);
        if (response && response.data) {
          this.notices = Array.isArray(response.data) ? response.data : [response.data];
          console.log('加载的最近公告数量:', this.notices.length);
        } else {
          console.warn('获取最近系统公告返回空数据');
          this.notices = [];
        }
      }).catch(error => {
        console.error('获取最近公告失败:', error);
        // 使用模拟数据
        this.notices = [
          {
            id: 'mock1',
            title: '欢迎使用心理健康系统',
            content: '这是一条模拟的系统公告，当API未正常工作时会显示。',
            createTime: new Date().toISOString(),
            updateTime: new Date().toISOString(),
            status: 1
          }
        ];
      });
    },
    formatDateTime(dateTime) {
      if (!dateTime) return '';
      const date = new Date(dateTime);
      return date.toLocaleString();
    },
    truncateContent(content, maxLength) {
      if (content.length > maxLength) {
        return content.slice(0, maxLength) + '...';
      }
      return content;
    }
  }
}
</script>

<style scoped>
.home-bg {
  min-height: 100vh;
  width: 100vw;
  position: fixed;
  top: 0;
  left: 0;
  z-index: -1;
  background: url('@/assets/shouye.png') no-repeat center center fixed;
  background-size: cover;
  filter: blur(2px) brightness(0.98);
}
.home-main {
  max-width: 1200px;
  margin: 0 auto;
  padding: 32px 0 0 0;
  min-height: 92vh;
  background: transparent;
  display: flex;
  flex-direction: column;
  gap: 20px;
}
.overlap-carousel-area,
.music-player-top,
.post-card {
  border-radius: 22px;
  box-shadow: 0 6px 32px rgba(52,152,219,0.13);
  border: 2px solid #e0e6ed;
  width: 100%;
  margin: 0;
}
.overlap-carousel-area {
  background: #fff;
  padding: 18px 0 18px 0;
  min-height: 320px;
  position: relative;
  overflow: hidden;
  display: flex;
  justify-content: center;
  align-items: center;
}
.overlap-carousel-wrapper {
  width: 100%;
  overflow: visible;
  position: relative;
  height: 420px;
  display: flex;
  justify-content: center;
}
.overlap-carousel-track {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0;
  position: relative;
  width: auto;
}
.overlap-carousel-img {
  position: relative;
  z-index: 1;
  margin-left: -70px;
  transition: filter 0.4s, transform 0.4s, z-index 0.2s, box-shadow 0.2s;
  filter: blur(1.2px) grayscale(30%) brightness(0.92);
  transform: scale(0.82);
  box-shadow: 0 2px 12px rgba(0,0,0,0.10);
  border-radius: 20px;
  cursor: pointer;
  overflow: hidden;
  border: 2.5px solid transparent;
}
.overlap-carousel-img:first-child {
  margin-left: 0;
}
.overlap-carousel-img img {
  width: 320px;
  height: 380px;
  object-fit: cover;
  border-radius: 18px;
  display: block;
  transition: transform 0.3s;
}
.overlap-carousel-img.active {
  filter: none;
  transform: scale(1.10) translateY(-10px);
  z-index: 2;
  box-shadow: 0 10px 40px rgba(52,152,219,0.18);
  border: 2.5px solid #3498db;
}
.overlap-carousel-img:hover {
  border: 2.5px solid #217dbb;
  box-shadow: 0 10px 40px rgba(52,152,219,0.22);
}
.overlap-carousel-track:hover .overlap-carousel-img:not(.active) {
  filter: blur(2.5px) grayscale(50%) brightness(0.8);
  transform: scale(0.78);
  z-index: 1;
}
.music-player {
  max-width: 1300px;
  margin: 0 auto;
  padding: 20px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 18px;
  box-shadow: 0 4px 24px rgba(0,0,0,0.08);
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.music-player-top {
  margin: 0;
  max-width: none;
  min-width: 0;
  background: #fff;
  padding: 18px 28px 18px 28px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  width: 100%;
  border-radius: 22px;
  box-shadow: 0 6px 32px rgba(52,152,219,0.13);
}
.music-info {
  display: flex;
  align-items: center;
  width: 100%;
  margin-bottom: 12px;
}
.cover-wrapper {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  overflow: hidden;
  margin-right: 18px;
  box-shadow: 0 2px 8px rgba(52,152,219,0.10);
  flex-shrink: 0;
}
.cover-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
}
.music-meta {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}
.music-title {
  font-size: 1.2rem;
  font-weight: 700;
  color: #217dbb;
  margin-bottom: 6px;
}
.music-artist {
  font-size: 0.9rem;
  color: #777;
}
.time-display {
  font-size: 0.9rem;
  color: #777;
  margin-bottom: 12px;
}
.progress-bar-bg {
  width: 100%;
  height: 10px;
  background: #e0e7ef;
  border-radius: 5px;
  overflow: hidden;
  cursor: pointer;
  margin-top: 8px;
  position: relative;
}
.progress-bar {
  height: 100%;
  background: linear-gradient(90deg, #4fc3f7, #2980b9);
  border-radius: 5px;
  transition: width 0.3s;
}
.progress-handle {
  width: 14px;
  height: 14px;
  border-radius: 50%;
  background: #fff;
  position: absolute;
  top: 50%;
  left: 0;
  transform: translate(-50%, -50%);
  box-shadow: 0 0 4px rgba(0, 0, 0, 0.2);
  border: 2px solid #3498db;
}
.music-controls {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 32px;
  margin-top: 8px;
  width: 100%;
}
.music-btn, .mode-btn, .volume-btn, .playlist-btn {
  background: linear-gradient(90deg, #f2f6fa 60%, #eaf2fb 100%);
  border: none;
  border-radius: 50%;
  width: 48px;
  height: 48px;
  font-size: 1.5rem;
  color: #2980b9;
  box-shadow: 0 2px 8px rgba(52,152,219,0.10);
  cursor: pointer;
  transition: background 0.2s, color 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}
.music-btn:hover, .mode-btn:hover, .volume-btn:hover, .playlist-btn:hover {
  background: linear-gradient(90deg, #3498db 60%, #4fc3f7 100%);
  color: #fff;
}
.music-extra-controls {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
}
.play-mode-control {
  display: flex;
  align-items: center;
}
.post-area {
  max-width: 1300px;
  margin: 0 auto;
}
.post-area-top {
  display: flex;
  justify-content: center;
  margin-top: 0;
}
.post-card {
  background: #fff;
  padding: 28px 36px;
  width: 100%;
  position: relative;
  overflow: hidden;
  margin: 0;
  border-radius: 22px;
  box-shadow: 0 6px 32px rgba(52,152,219,0.13);
}
.post-card h2 {
  font-size: 2rem;
  color: #217dbb;
  font-weight: 700;
  margin-bottom: 18px;
  position: relative;
  z-index: 2;
  border-left: 7px solid #3498db;
  padding-left: 14px;
  background: linear-gradient(90deg, #eaf2fb 60%, transparent);
  border-radius: 8px;
}
.post-pre {
  font-size: 1.08rem;
  color: #444;
  background: none;
  border: none;
  padding: 0;
  margin: 0;
  line-height: 1.9;
  white-space: pre-wrap;
  word-break: break-all;
  font-family: inherit;
  position: relative;
  z-index: 2;
}
@media (max-width: 900px) {
  .home-main {
    max-width: 100vw;
    padding: 8px 2vw 0 2vw;
    gap: 12px;
  }
  .overlap-carousel-area {
    min-height: 180px;
    padding: 8px 0;
    border-radius: 14px;
  }
  .overlap-carousel-img img {
    width: 120px;
    height: 140px;
    border-radius: 14px;
  }
  .overlap-carousel-wrapper {
    height: 140px;
  }
  .music-player-top {
    max-width: 98vw;
    padding: 6px 2vw 8px 2vw;
    border-radius: 14px;
  }
  .post-card {
    padding: 8px 4px;
    max-width: 98vw;
    border-radius: 14px;
  }
  .post-card h2 {
    font-size: 1.1rem;
    padding-left: 6px;
    border-radius: 4px;
  }
}

/* 添加新的样式 */
.detail-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.75);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  padding: 20px;
}

.detail-content {
  background: #fff;
  border-radius: 20px;
  display: flex;
  max-width: 1200px;
  width: 90%;
  height: 85vh;
  position: relative;
  overflow: hidden;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.detail-image {
  flex: 0 0 50%;
  overflow: hidden;
  background: #f8f9fa;
}

.detail-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.detail-text {
  flex: 1;
  padding: 40px;
  overflow-y: auto;
}

.detail-text h3 {
  font-size: 2rem;
  color: #2c3e50;
  margin-bottom: 20px;
  font-weight: 600;
}

.detail-subtitle {
  font-size: 1.2rem;
  color: #7f8c8d;
  margin-bottom: 30px;
  line-height: 1.6;
}

.detail-section {
  margin-bottom: 30px;
}

.detail-section h4 {
  font-size: 1.3rem;
  color: #2980b9;
  margin-bottom: 15px;
  font-weight: 500;
}

.detail-section p {
  font-size: 1.1rem;
  color: #34495e;
  line-height: 1.8;
  margin-bottom: 15px;
}

.detail-section ul {
  list-style: none;
  padding: 0;
}

.detail-section li {
  font-size: 1.1rem;
  color: #34495e;
  margin-bottom: 12px;
  padding-left: 24px;
  position: relative;
  line-height: 1.6;
}

.detail-section li::before {
  content: "•";
  color: #3498db;
  font-size: 1.5em;
  position: absolute;
  left: 0;
  top: -3px;
}

.close-btn {
  position: absolute;
  top: 20px;
  right: 20px;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: none;
  background: rgba(255, 255, 255, 0.9);
  color: #2c3e50;
  font-size: 24px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.close-btn:hover {
  background: #e74c3c;
  color: #fff;
}

/* 添加禁止滚动样式 */
:global(body.modal-open) {
  overflow: hidden;
}

@media (max-width: 768px) {
  .detail-content {
    flex-direction: column;
    height: 90vh;
  }

  .detail-image {
    flex: 0 0 40%;
  }

  .detail-text {
    padding: 20px;
  }

  .detail-text h3 {
    font-size: 1.6rem;
  }

  .detail-subtitle {
    font-size: 1.1rem;
  }

  .detail-section h4 {
    font-size: 1.2rem;
  }

  .detail-section p,
  .detail-section li {
    font-size: 1rem;
  }
}

/* 添加新的样式 */
.playlist-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.75);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  padding: 20px;
}

.playlist-content {
  background: #fff;
  border-radius: 20px;
  max-width: 500px;
  width: 90%;
  height: 70vh;
  position: relative;
  overflow: hidden;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
  display: flex;
  flex-direction: column;
}

.playlist-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #eee;
  flex-shrink: 0;
}

.playlist-header h3 {
  font-size: 1.3rem;
  color: #2980b9;
  font-weight: 700;
  margin: 0;
}

.close-playlist-btn {
  background: none;
  border: none;
  font-size: 24px;
  color: #2c3e50;
  cursor: pointer;
  padding: 0;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.playlist-items {
  flex: 1;
  overflow-y: auto;
  padding: 0;
}

.playlist-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  cursor: pointer;
  border-bottom: 1px solid #f5f5f5;
  transition: background-color 0.2s;
}

.playlist-item:hover {
  background: #f8f9fa;
}

.playlist-item.active {
  background: #e3f2fd;
  border-left: 4px solid #2980b9;
}

.playlist-item-info {
  display: flex;
  align-items: center;
  flex: 1;
}

.playlist-item-cover {
  width: 50px;
  height: 50px;
  object-fit: cover;
  border-radius: 8px;
  margin-right: 15px;
}

.playlist-item-details {
  display: flex;
  flex-direction: column;
  flex: 1;
}

.playlist-item-title {
  font-size: 1rem;
  color: #2c3e50;
  font-weight: 600;
  margin-bottom: 4px;
}

.playlist-item-artist {
  font-size: 0.85rem;
  color: #777;
}

.playlist-item-status {
  font-size: 1.2rem;
  color: #2980b9;
  margin-left: 10px;
}

.playing-indicator {
  color: #4fc3f7;
  animation: pulse 1.5s infinite;
}

.paused-indicator {
  color: #777;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

/* 系统公告区域样式 */
.notice-area {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  margin: 20px auto;
  padding: 15px;
  max-width: 1200px;
}

.notice-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
}

.notice-header h3 {
  margin: 0;
  color: #333;
  font-size: 18px;
}

.notice-list {
  display: flex;
  flex-direction: column;
}

.notice-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px dashed #eee;
  cursor: pointer;
  transition: background-color 0.3s;
}

.notice-item:hover {
  background-color: #f9f9f9;
}

.notice-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #333;
}

.notice-time {
  font-size: 12px;
  color: #999;
}

.notice-content-preview {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  max-height: 40px;
}

.notice-detail .notice-header {
  border-bottom: 1px solid #eee;
  padding-bottom: 15px;
  margin-bottom: 15px;
}

.notice-detail .notice-header h2 {
  margin: 0 0 10px 0;
  font-size: 20px;
  color: #333;
}

.notice-detail .notice-meta {
  display: flex;
  gap: 20px;
  font-size: 12px;
  color: #999;
}

.notice-detail .notice-content {
  line-height: 1.6;
  color: #333;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.notice-banner {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  margin: 20px auto;
  padding: 15px;
  max-width: 1200px;
}

.notice-banner-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.notice-icon {
  font-size: 24px;
  color: #2980b9;
  margin-right: 10px;
}

.notice-carousel {
  flex: 1;
}

.notice-more {
  margin-left: 10px;
}

.notice-item-banner {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px dashed #eee;
  cursor: pointer;
  transition: background-color 0.3s;
}

.notice-item-banner:hover {
  background-color: #f9f9f9;
}

.notice-title-text {
  font-size: 14px;
  color: #333;
}

.notice-time-text {
  font-size: 12px;
  color: #999;
}

/* 重新设计的公告区样式 */
.announcement-section {
  background: linear-gradient(135deg, #3498db, #1a5276);
  border-radius: 8px;
  margin: 15px auto;
  max-width: 1200px;
  overflow: hidden;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  position: relative;
  transition: all 0.3s ease;
}

.announcement-section:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15);
}

.announcement-container {
  display: flex;
  align-items: center;
  padding: 12px 20px;
  color: white;
}

.announcement-icon {
  margin-right: 15px;
  position: relative;
}

.bell-icon {
  font-size: 22px;
  animation: ring 4s ease-in-out infinite;
  display: flex;
  align-items: center;
  justify-content: center;
}

@keyframes ring {
  0%, 100% {
    transform: rotate(0deg);
  }
  5%, 15% {
    transform: rotate(15deg);
  }
  10%, 20% {
    transform: rotate(-15deg);
  }
  25% {
    transform: rotate(0deg);
  }
}

.announcement-content {
  flex: 1;
  overflow: hidden;
  position: relative;
}

.announcement-item {
  font-size: 16px;
  line-height: 36px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s;
}

.announcement-item:hover {
  transform: translateX(5px);
}

.announcement-action {
  margin-left: 20px;
  white-space: nowrap;
}

/* 弹出框样式优化 */
.announcement-detail {
  padding: 10px;
}

.announcement-detail-header h2 {
  margin: 0 0 15px 0;
  color: #333;
  font-size: 22px;
  font-weight: 600;
}

.announcement-detail-meta {
  display: flex;
  gap: 15px;
  color: #666;
  font-size: 14px;
  margin-bottom: 20px;
}

.announcement-detail-divider {
  height: 1px;
  background: linear-gradient(90deg, rgba(52, 152, 219, 0.8), rgba(52, 152, 219, 0.2));
  margin: 15px 0 20px 0;
}

.announcement-detail-content {
  line-height: 1.8;
  color: #333;
  font-size: 15px;
}

.announcement-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.announcement-card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 15px;
  cursor: pointer;
  transition: transform 0.3s;
}

.announcement-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 40px rgba(52, 152, 219, 0.2);
}

.announcement-card-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.announcement-card-content {
  font-size: 15px;
  color: #333;
}

.announcement-card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
}

.announcement-card-time {
  font-size: 14px;
  color: #999;
}
</style>

