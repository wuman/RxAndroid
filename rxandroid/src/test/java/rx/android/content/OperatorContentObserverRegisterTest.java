/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rx.android.content;

import android.app.Activity;
import android.content.ContentResolver;
import android.net.Uri;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.robolectric.RobolectricTestRunner;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.observers.TestObserver;

import static android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@RunWith(RobolectricTestRunner.class)
public class OperatorContentObserverRegisterTest {

    private ContentResolver contentResolver;

    @Before
    public void setUp() throws Exception {
        this.contentResolver = new Activity().getContentResolver();
    }

    @Test
    public void testUriChanges() {
        Observable<Uri> observable
            = ContentObservable.fromContentObserver(contentResolver, EXTERNAL_CONTENT_URI);
        final Observer<Uri> observer = mock(Observer.class);
        final Subscription subscription = observable.subscribe(new TestObserver<Uri>(observer));

        final InOrder inOrder = inOrder(observer);

        inOrder.verify(observer, never()).onNext(any(Uri.class));

        contentResolver.notifyChange(EXTERNAL_CONTENT_URI, null);
        inOrder.verify(observer, times(1)).onNext(EXTERNAL_CONTENT_URI);

        contentResolver.notifyChange(EXTERNAL_CONTENT_URI, null);
        inOrder.verify(observer, times(1)).onNext(EXTERNAL_CONTENT_URI);

        subscription.unsubscribe();
        contentResolver.notifyChange(EXTERNAL_CONTENT_URI, null);
        inOrder.verify(observer, never()).onNext(any(Uri.class));

        inOrder.verify(observer, never()).onError(any(Throwable.class));
        inOrder.verify(observer, never()).onCompleted();
    }

}
