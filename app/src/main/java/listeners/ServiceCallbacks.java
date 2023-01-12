package listeners;

import managers.PlaybackManager;
import managers.QueueManager;

public interface ServiceCallbacks {
    void setQueueManager(QueueManager queueManager);
    void setPlaybackManager(PlaybackManager playbackManager);
}
