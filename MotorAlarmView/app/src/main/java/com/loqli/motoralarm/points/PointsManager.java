package com.loqli.motoralarm.points;

import android.widget.Switch;

import java.io.IOException;
import java.util.List;

import com.loqli.motoralarm.model.ActiveResult;
import com.loqli.motoralarm.model.Point;
import ch.boye.httpclientandroidlib.client.ClientProtocolException;

public interface PointsManager {

	public List<Point> getPoints() throws ClientProtocolException, IOException;

	public int activate(boolean b);

    ActiveResult isDeviceActive();
}
