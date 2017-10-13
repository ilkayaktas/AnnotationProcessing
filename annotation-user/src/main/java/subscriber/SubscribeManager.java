package subscriber;

import annotations.AbstractQuosType;
import annotations.SubscriberAnnotation;

/**
 * Created by iaktas on 02.10.2017 at 14:13.
 */

public class SubscribeManager {

    @SubscriberAnnotation(QuosType = AbstractQuosType.AlarmEventQos)
    Subscriber<SubsysStatus> subsysStatusSubscriber;

    @SubscriberAnnotation(QuosType = AbstractQuosType.ContinuousQos)
    Subscriber<SourceTrackData> sourceTrackDataSubscriber;

}
